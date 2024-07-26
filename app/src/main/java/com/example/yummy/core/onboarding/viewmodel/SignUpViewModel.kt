package com.example.yummy.core.onboarding.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.SignupLoginRepository
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Resource.Loading
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signupLoginRepository: SignupLoginRepository
) : ViewModel() {

    private val _signupUserResponse = MutableLiveData<Resource<FirebaseUser>?>()
    val signupUserResponse: MutableLiveData<Resource<FirebaseUser>?> = _signupUserResponse
    private val _testResponse = MutableLiveData<String>()
    val testResponse: LiveData<String> = _testResponse



    @OptIn(ExperimentalCoroutinesApi::class)
    fun executeOnboardNewUser(userEmail: String?, password: String?) {
        _signupUserResponse.postValue(Loading())

        viewModelScope.launch {
            signupLoginRepository.executeOnboardNewUsers(userEmail, password).collect {
                when {
                    it.isSuccess -> {
                        _signupUserResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        Log.d("Failure block", it.getOrNull().toString())
                        _signupUserResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }











//    fun executeOnboardNew(userEmail: String, password: String) {
//        _signupUserResponse.postValue(Loading())
//
//        viewModelScope.launch {
//            val response =
//                signupLoginRepository.executeOnboard(userEmail, password).collect{ values ->
//                    _signupUserResponse.postValue(values)
//
////                    _signupUserResponse.value = values
//
//                }
//        }

    }

