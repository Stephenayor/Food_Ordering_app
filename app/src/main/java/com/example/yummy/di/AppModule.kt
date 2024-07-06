package com.example.yummy.di

import android.app.Application
import android.content.Context
import com.example.yummy.data.SignupLoginRepository
import com.example.yummy.di.module.ViewModelFactoryModule
import com.example.yummy.utils.MyApplication
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
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
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideSignupLoginRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        context: Context
    ): SignupLoginRepository {
        return SignupLoginRepository(firebaseAuth, firestore, context)
    }

    @Provides
    fun provideViewModelFactoryModule(): KClass<ViewModelFactoryModule> {
        return ViewModelFactoryModule::class
    }
}