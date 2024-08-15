package com.example.yummy.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yummy.data.repository.model.Product
import com.example.yummy.data.repository.model.local.ProductsEntity


@Database(entities = [ProductsEntity::class], version = 2, exportSchema = false)
abstract class YummyDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
