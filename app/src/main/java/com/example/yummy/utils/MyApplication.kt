package com.example.yummy.utils

import android.app.Application
import co.paystack.android.PaystackSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Initialize Paystack
        PaystackSdk.initialize(applicationContext)

    }
}