package com.example.yummy.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.yummy.data.repository.model.Product
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AddProductsRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {

    @ExperimentalCoroutinesApi
    fun addImageToCloudStorage(imageUri: Uri) =
        callbackFlow<Result<Resource<Uri>>> {
            val firebaseStorageReference = firebaseStorage.reference
            val imageRef = firebaseStorageReference.child("images/${UUID.randomUUID()}.jpg")
            try {
                val downloadUrl = imageRef.child(AppConstants.IMAGE_NAME)
                    .putFile(imageUri).await()
                    .storage.downloadUrl.await()
                val item = Resource.Success(downloadUrl)
                this@callbackFlow.trySend(Result.success(item))
            } catch (e: Exception) {
                val nullData: Uri? = null
                val uriError = Resource.Error(e.localizedMessage, nullData)
                this@callbackFlow.trySend(Result.success(uriError))
            }
            awaitClose {}
        }

    @ExperimentalCoroutinesApi
    fun addProductsToFireStoreDatabase(downloadUrl: Uri, productName: String, price: Double) =
        callbackFlow<Result<Resource<Boolean>>> {
            try {
                val productDetails = HashMap<String, Any>()
                productDetails["productImage"] = downloadUrl
                productDetails["productName"] = productName
                productDetails["productPrice"] = price
                firebaseFirestore.collection(AppConstants.PRODUCT).document(AppConstants.PRODUCT_ID)
                    .set(productDetails)
                    .await()
                val item = Resource.Success(true)
                this@callbackFlow.trySend(Result.success(item))
            } catch (e: Exception) {
                Log.d("Firestore exception", "add products to Firestore Failed")
                val error = Resource.Error(e.localizedMessage, false)
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}
        }

    @ExperimentalCoroutinesApi
    fun getProductsFromFireStoreDatabase() =
        callbackFlow<Result<Resource<Product>>> {
            val firebaseStorageReference = firebaseStorage.reference
            try {
               val firestoreDocumentReference =
                firebaseFirestore.collection(AppConstants.PRODUCT).document().get()
                    .await()
                val product = firestoreDocumentReference.toObject(Product::class.java)
                val result = Resource.Success(product)
                this@callbackFlow.trySend(Result.success(result))
            } catch (e: Exception) {
                Tools.showToast(context, "getting products from Firestore Failed")
                val nullData: Product? = null
                val error = Resource.Error(e.localizedMessage, nullData)
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}
        }
}