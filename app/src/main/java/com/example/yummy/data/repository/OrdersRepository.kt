package com.example.yummy.data.repository

import android.content.Context
import com.example.yummy.data.repository.model.CartItem
import com.example.yummy.data.repository.model.Orders
import com.example.yummy.utils.AppConstants.ORDERS
import com.example.yummy.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrdersRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseFireStore: FirebaseFirestore
) {

    @ExperimentalCoroutinesApi
    suspend fun addProductsToOrderDB(cartItems: List<CartItem>, dateOfOrder: Long) =
        callbackFlow<Result<Resource<Boolean>>> {

            try {
                val collectionRef = firebaseFireStore.collection(ORDERS)

                for (cartItem in cartItems) {
                    val orderData = hashMapOf(
                        "id" to cartItem.id,
                        "product" to cartItem.product,
                        "creationDate" to cartItem.creationDate,
                        "quantity" to cartItem.quantity,
                        "dateOfOrder" to dateOfOrder
                    )

                    // Add the document to the collection
                    collectionRef.add(orderData)
                        .addOnSuccessListener {
                            val result = Resource.Success(true)
                            this@callbackFlow.trySend(Result.success(result))
                        }
                        .addOnFailureListener { e ->
                            trySend(
                                Result.success(
                                    Resource.Error(
                                        e.localizedMessage ?: "Unknown error",
                                        false
                                    )
                                )
                            )

                        }
                }

            } catch (e: Exception) {
                val error = Resource.Error(e.localizedMessage, false)
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}
        }

    @ExperimentalCoroutinesApi
    suspend fun getAllOrdersFromFireStore(
        lastVisible: DocumentSnapshot? = null, // Last visible document for pagination
        pageSize: Long = 10 // Number of items to load per page
    ) = callbackFlow<Result<Resource<List<Orders>>>> {
        val ordersList: MutableList<Orders> = ArrayList()
        try {
            var query = firebaseFireStore.collection(ORDERS).limit(pageSize)

            if (lastVisible != null) {
                query = query.startAfter(lastVisible)
            }

            query.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lastVisibleDocument = task.result.documents.lastOrNull()
                        for (document in task.result) {
                            val product = document.toObject(Orders::class.java)
                            ordersList.add(product)
                        }
                        val result = Resource.Success(ordersList.toList())
                        this@callbackFlow.trySend(Result.success(result))
                    } else {
                        val error = Resource.Error<List<Orders>>(task.exception?.localizedMessage)
                        this@callbackFlow.trySend(Result.success(error))
                    }
                }.await()
        } catch (e: Exception) {
            val error = Resource.Error<List<Orders>>(e.localizedMessage)
            this@callbackFlow.trySend(Result.success(error))
        }
        awaitClose {}
    }

   suspend fun getOrdersByDateRange(startDate: Long, endDate: Long) =
        callbackFlow<Result<Resource<List<Orders>>>> {
            try {
                val query = firebaseFireStore.collection(ORDERS)
                    .whereGreaterThanOrEqualTo("dateOfOrder", startDate)
                    .whereLessThanOrEqualTo("dateOfOrder", endDate)

                val listenerRegistration = query.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        trySend(Result.success(Resource.Error(e.localizedMessage, null))).isSuccess
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val orders = snapshot.toObjects(Orders::class.java)
                        val result = Resource.Success(orders)
                        this@callbackFlow.trySend(Result.success(result))
//                    trySend(Result.success(Resource.Success(orders))).isSuccess
                    } else {
                        val result = Resource.Success(emptyList<Orders>())
                        this@callbackFlow.trySend(Result.success(result))
//                    trySend(Result.success(Resource.Success(emptyList()))).isSuccess
                    }
                }

                awaitClose { listenerRegistration.remove() }
            } catch (e: Exception) {
                trySend(Result.success(Resource.Error(e.localizedMessage, null))).isFailure
                val error = Resource.Error<List<Orders>>(e.localizedMessage)
                this@callbackFlow.trySend(Result.success(error))
            }
        }
}