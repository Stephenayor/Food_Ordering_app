package com.example.yummy.data.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yummy.data.repository.model.Product
import com.example.yummy.data.repository.model.local.ProductsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
     fun getAllProducts(): Flow<ProductsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductsEntity>)


    @Query("DELETE FROM products")
    suspend fun clearAllProducts()
}
