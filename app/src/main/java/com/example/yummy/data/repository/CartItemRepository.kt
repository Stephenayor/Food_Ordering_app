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
import kotlinx.coroutines.flow.Flow
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
                val database = FirebaseDatabase.getInstance().reference
                // Create a unique key for each cart item
                val cartItemId = database.child(CARTS).child(userId.toString()).push().key
                cartItemId?.let {
                    val cartItem =
                        CartItem(
                            product = product,
                            userId = userId.toString(),
                            creationDate = System.currentTimeMillis(),
                            quantity = quantity
                        )

                    // Save the cart item under the user's cart in the database
                    database.child(CARTS).child(userId.toString()).child(cartItemId.toString())
                        .setValue(cartItem)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Cart item saved successfully.")
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

    fun getCartItems(userId: String) = callbackFlow<Result<Resource<List<CartItem>>>> {
        try {
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(CARTS)
            val cartItemsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cartItems = snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
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

        awaitClose {  }
    }

}