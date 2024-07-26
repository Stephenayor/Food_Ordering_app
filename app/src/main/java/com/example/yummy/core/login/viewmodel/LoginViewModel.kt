package com.example.yummy.core.login.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.SignupLoginRepository
import com.example.yummy.utils.Resource
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel@Inject constructor(
    private val signupLoginRepository: SignupLoginRepository
) : ViewModel()
{
    private val _loginUserResponse = MutableLiveData<Resource<FirebaseUser>?>()
    val loginResponse: MutableLiveData<Resource<FirebaseUser>?> = _loginUserResponse


    @OptIn(ExperimentalCoroutinesApi::class)
    fun executeLogin(userEmail: String?, password: String?) {
        _loginUserResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            signupLoginRepository.login(userEmail, password).collect {
                when {
                    it.isSuccess -> {
                        _loginUserResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        Log.d("Login Failure", it.getOrNull().toString())
                        _loginUserResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun firebaseAuthWithGoogleAccount(idToken: String) {
        _loginUserResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            signupLoginRepository.firebaseAuthenticationWithGoogleAccount(idToken).collect {
                when {
                    it.isSuccess -> {
                        _loginUserResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        _loginUserResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }


}