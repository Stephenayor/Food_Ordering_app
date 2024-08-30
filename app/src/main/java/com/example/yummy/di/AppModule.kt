package com.example.yummy.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.yummy.core.admin.home.adapter.HomeFragmentProductsAdapter
import com.example.yummy.core.user.home.adapter.UserHomeFragmentsProductAdapter
import com.example.yummy.core.user.orders.adapter.CartItemAdapter
import com.example.yummy.data.repository.AddProductsRepository
import com.example.yummy.data.repository.CartItemRepository
import com.example.yummy.data.repository.OrdersRepository
import com.example.yummy.data.repository.SignupLoginRepository
import com.example.yummy.data.repository.database.ProductDao
import com.example.yummy.data.repository.database.YummyDatabase
import com.example.yummy.di.module.ViewModelFactoryModule
import com.example.yummy.utils.AppConstants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRealtimeDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseCloudStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            AppConstants.ENCRYPTED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    fun provideSignupLoginRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        context: Context,
        sharedPreferences: SharedPreferences
    ): SignupLoginRepository {
        return SignupLoginRepository(firebaseAuth, firestore, context, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): YummyDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            YummyDatabase::class.java,
            "yummy_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: YummyDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    fun provideAddProductsRepository(
        firebaseStorage: FirebaseStorage,
        firebaseFirestore: FirebaseFirestore,
        context: Context,
        productDao: ProductDao
    ): AddProductsRepository {
        return AddProductsRepository(firebaseStorage, firebaseFirestore, context, productDao)
    }

    @Provides
    fun provideCartsItemRepository(
        firebaseStorage: FirebaseStorage,
        firebaseFirestore: FirebaseFirestore,
        context: Context,
        firebaseDatabase: FirebaseDatabase
    ): CartItemRepository {
        return CartItemRepository(firebaseStorage, firebaseFirestore,firebaseDatabase, context)
    }

    @Provides
    fun provideOrdersRepository(
        context: Context,
        firebaseFireStore: FirebaseFirestore
    ): OrdersRepository {
        return OrdersRepository(context, firebaseFireStore)
    }

    @Provides
    fun provideViewModelFactoryModule(): KClass<ViewModelFactoryModule> {
        return ViewModelFactoryModule::class
    }

    @Provides
    fun provideHomeFragmentProductsAdapter(
        @ApplicationContext context: Context
    ): HomeFragmentProductsAdapter {
        return HomeFragmentProductsAdapter(context)
    }

    @Provides
    fun provideUserHomeFragmentProductsAdapter(
        @ApplicationContext context: Context
    ): UserHomeFragmentsProductAdapter {
        return UserHomeFragmentsProductAdapter(context)
    }

    @Provides
    fun provideCartItemAdapter(
        @ApplicationContext context: Context
    ): CartItemAdapter {
        return CartItemAdapter(context)
    }




}