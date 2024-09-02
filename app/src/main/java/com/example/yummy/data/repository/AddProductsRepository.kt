package com.example.yummy.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.yummy.data.repository.database.ProductDao
import com.example.yummy.data.repository.model.Product
import com.example.yummy.data.repository.model.local.ProductsEntity
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.AppConstants.PRODUCT
import com.example.yummy.utils.AppConstants.PRODUCT_ID
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


class AddProductsRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFireStore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
    val productDao: ProductDao
) {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var foodProducts: List<Product>
    private lateinit var firebaseAuth: FirebaseAuth


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
                firebaseFireStore.collection(PRODUCT).document()
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
    suspend fun getLatestProductsFromFireStoreDatabase() =
        callbackFlow<Result<Resource<Product>>> {
            try {
                val fireStoreDocumentReference =
                    firebaseFireStore.collection(PRODUCT)
                        .document(PRODUCT_ID)
                        .get()
                        .await()
                val product = fireStoreDocumentReference.toObject(Product::class.java)
                if (product != null) {
                    // Save the product in the room database and fetch the saved product
                    withContext(Dispatchers.IO) {
                        productDao.clearAllProducts()
                        saveProductsInDB(listOf(product))
                        getProductsFromDB().collect { productsEntity ->
                            foodProducts = productsEntity.toProductList()
                            val result = Resource.Success(foodProducts[0])
                            this@callbackFlow.trySend(Result.success(result))
                        }
                    }
                } else {
                    getProductsFromDB().collect { productsEntity ->
                        foodProducts = productsEntity.toProductList()
                        if (foodProducts[0].productName.isNotEmpty()) {
                            val result = Resource.Success(foodProducts[0])
                            this@callbackFlow.trySend(Result.success(result))
                        }
                    }
                }
            } catch (e: Exception) {
                Tools.showToast(context, "getting products from Firestore Failed")
                val nullData: Product? = null
                val error = Resource.Error(e.localizedMessage, nullData)
                Log.d("errorMessage", e.localizedMessage.toString())
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}
        }

    @ExperimentalCoroutinesApi
    suspend fun getAllProductsFromFireStore() =
        callbackFlow<Result<Resource<List<Product>>>> {
            val productsList: MutableList<Product> = ArrayList()
            try {
                firebaseFireStore.collection(PRODUCT)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                document.id
                                val product = document.toObject(Product::class.java)
                                productsList.add(product)
                            }
                        }
                    }
                    .await()
                withContext(Dispatchers.IO) {
                    productDao.clearAllProducts()
                    saveProductsInDB(productsList.toList())
                    getProductsFromDB().collect { productsEntity ->
                        foodProducts = productsEntity.toProductList()
                        val result = Resource.Success(foodProducts)
                        this@callbackFlow.trySend(Result.success(result))
                    }
                }
            } catch (e: Exception) {
                val nullData: List<Product>? = null
                val error = Resource.Error(e.localizedMessage, nullData)
                Log.d("errorMessage", e.localizedMessage.toString())
                this@callbackFlow.trySend(Result.success(error))
            }
            awaitClose {}
        }


    private fun Product.toProductEntity(): ProductsEntity {
        return ProductsEntity(
            productImage = this.productImage,
            productName = this.productName,
            productPrice = this.productPrice
        )
    }

    private fun List<Product>.toProductEntityList(): List<ProductsEntity> {
        return this.map { product ->
            ProductsEntity(
                productImage = product.productImage,
                productName = product.productName,
                productPrice = product.productPrice
            )
        }
    }


    private fun ProductsEntity.toProduct(): Product {
        return Product(
            productImage = this.productImage,
            productName = this.productName,
            productPrice = this.productPrice
        )
    }

    private fun List<ProductsEntity>.toProductList(): List<Product> {
        return this.map { product ->
            Product(
                productImage = product.productImage,
                productName = product.productName,
                productPrice = product.productPrice
            )
        }
    }


    private suspend fun saveProductsInDB(product: List<Product>) {
        val productEntity = product.toProductEntityList()
        productDao.insertProducts((productEntity))
    }

    private fun getProductsFromDB(): Flow<List<ProductsEntity>> {
        return productDao.getAllProducts()
    }


    fun cancelJob() {
        job.cancel()
    }

}