package com.example.yummy.data.repository.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductsEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_image") val productImage: String,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "product_price") val productPrice: Double
)