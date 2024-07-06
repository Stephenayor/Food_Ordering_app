package com.example.yummy.di.module

import androidx.lifecycle.ViewModelProvider
import com.example.yummy.utils.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelFactoryModule {
    @Binds
    @Named("SignupFragment")
    abstract fun bindSignUpViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}


