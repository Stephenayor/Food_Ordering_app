package com.example.yummy.di

import com.example.yummy.utils.MyApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [])
interface AppComponent {

    fun inject(app: MyApplication?)
}