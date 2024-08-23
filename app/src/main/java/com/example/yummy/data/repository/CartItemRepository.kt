package com.example.yummy.data.repository

import android.content.Context
import android.util.Log
import com.example.yummy.data.repository.model.CartItem
import com.example.yummy.data.repository.model.Product
import com.example.yummy.utils.AppConstants.CARTS
import com.example.yummy.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CartItemRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseRealTimeDatabase: FirebaseDatabase,
    @ApplicationContext private val context: Context,
) {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var firebaseAuth: FirebaseAuth


    @ExperimentalCoroutinesApi
    fun addCartItemToRealtimeDatabase(product: Product, quantity: String) =
        callbackFlow<Result<Resource<Boolean>>> {
            firebaseAuth = FirebaseAuth.getInstance()
            val userId = firebaseAuth.currentUser?.uid
            try {
                val database = firebaseRealTimeDatabase.reference
                // Create a id for each cart item
                val cartItemId = database.child(CARTS).child(userId.toString()).push().key
                cartItemId?.let {
                    val cartItem =
                        CartItem(
                            id = cartItemId,
                            product = product,
                            userId = userId.toString(),
                            creationDate = System.currentTimeMillis(),
                            quantity = quantity
                        )
                    database.child(CARTS).child(userId.toString()).child(cartItemId.toString())
                        .setValue(cartItem)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Cart item updated successfully.")
                            val result = Resource.Success(true)
                            this@callbackFlow.trySend(Result.success(result))
                        }
                        .addOnFailureListener { e: Throwable ->
                            Log.e("Firebasedatabase", "Failed to save cart item.", e)
                            val error = Resource.Error(e.localizedMessage, false)
                            this@callbackFlow.trySend(Result.success(error))
                        }
                }
            } catch (e: Exception) {
                Log.d("FirebaseDatabase exception", e.localizedMessage.toString())
                val error = Resource.Error(e.localizedMessage, false)
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}

        }


    @ExperimentalCoroutinesApi
    fun updateCartItemInRealtimeDatabase(cartItem: CartItem) =
        callbackFlow<Result<Resource<Boolean>>> {
            firebaseAuth = FirebaseAuth.getInstance()
            val userId = firebaseAuth.currentUser?.uid
            try {
                val database = firebaseRealTimeDatabase.reference
                val cartItemsRef = database.child(CARTS).child(userId.toString())
                val existingProductCartId = cartItem.id

                // Create a map with the fields you want to update
                val updates: HashMap<String, Any?> = if (cartItem.quantity.toInt() == 0){
                    hashMapOf(
                        "$existingProductCartId/id" to null,
                        "$existingProductCartId/quantity" to null,
                        "$existingProductCartId/product" to null,
                        "$existingProductCartId/creationDate" to null,
                        "$existingProductCartId/userId" to null
                    )
                }else{
                    hashMapOf(
                        "$existingProductCartId/quantity" to cartItem.quantity,
                        "$existingProductCartId/product" to cartItem.product,
                        "$existingProductCartId/creationDate" to System.currentTimeMillis()
                    )
                }
                cartItemsRef.updateChildren(updates)
                    .addOnSuccessListener {
                        val result = Resource.Success(true)
                        this@callbackFlow.trySend(Result.success(result))
                    }
                    .addOnFailureListener { e: Throwable ->
                        val error = Resource.Error(e.localizedMessage, false)
                        this@callbackFlow.trySend(Result.success(error))
                    }
            } catch (e: Exception) {
                Log.d("FirebaseDatabase exception", e.localizedMessage.toString())
                val error = Resource.Error(e.localizedMessage, false)
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}
        }


    fun getCartItems(userId: String) = callbackFlow<Result<Resource<List<CartItem>>>> {
        try {
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(CARTS)
            val cartItemsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cartItems =
                        snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
                    val result = Resource.Success(cartItems)
                    this@callbackFlow.trySend(Result.success(result))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Firebase exception", error.message)
                    val nullData: List<CartItem>? = null
                    val errorMessage = Resource.Error(error.message, nullData)
                    this@callbackFlow.trySend(Result.success(errorMessage))
                }
            }
            databaseReference.child(userId).addValueEventListener(cartItemsListener)
        } catch (exception: Exception) {
            Log.d("FirebaseDatabase exception", exception.localizedMessage.toString())
            val nullData: List<CartItem>? = null
            val error = Resource.Error(exception.localizedMessage, nullData)
            this@callbackFlow.trySend(Result.success(error))
        }

        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    fun deleteAllItemsInCart() =
        callbackFlow<Result<Resource<Boolean>>> {
            firebaseAuth = FirebaseAuth.getInstance()
            val userId = firebaseAuth.currentUser?.uid
            try {
                val database = firebaseRealTimeDatabase.reference
                val cartItemsRef = database.child(CARTS).child(userId.toString())

                cartItemsRef.removeValue()
                    .addOnSuccessListener {
                        val result = Resource.Success(true)
                        this@callbackFlow.trySend(Result.success(result))
                    }
                    .addOnFailureListener { e: Throwable ->
                        val error = Resource.Error(e.localizedMessage, false)
                        this@callbackFlow.trySend(Result.success(error))
                    }
            } catch (e: Exception) {
                val error = Resource.Error(e.localizedMessage, false)
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}
        }


}