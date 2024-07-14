package com.example.yummy.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yummy.data.repository.SignupLoginRepository
import com.example.yummy.core.onboarding.viewmodel.SignUpViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(
    private val signupLoginRepository: SignupLoginRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> SignUpViewModel(
                signupLoginRepository
            ) as T

            else -> throw IllegalArgumentException("Unknown viewModel class $modelClass")
        }
    }
}