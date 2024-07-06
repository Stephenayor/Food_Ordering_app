package com.example.yummy.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.SignupLoginRepository
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Resource.Loading
import com.example.yummy.utils.Tools
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signupLoginRepository: SignupLoginRepository
) : ViewModel() {

    private val _signupUserResponse = MutableLiveData<Resource<FirebaseUser>>()
    private val signupUserResponseLiveData = MutableLiveData<Resource<FirebaseUser>>()
    val signupUserResponse: LiveData<Resource<FirebaseUser>> = _signupUserResponse
    private val _testResponse = MutableLiveData<String>()
    val testResponse: LiveData<String> = _testResponse


    fun executeOnboardNewUser(userEmail: String, password: String) {
        _signupUserResponse.postValue(Loading())

        viewModelScope.launch {
            val response =
                signupLoginRepository.executeOnboardNewUsers(userEmail, password).collect{values ->
                    _signupUserResponse.postValue(values)

//                    _signupUserResponse.value = values
                }

//
//            val response =
//                signupLoginRepository.sampleFlow(userEmail, password).asLiveData()
//            _testResponse.postValue(response.value)
        }

    }
}
