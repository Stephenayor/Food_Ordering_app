package com.example.yummy.core.admin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.SignupLoginRepository
import com.example.yummy.data.repository.model.Product
import com.example.yummy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AdminSharedViewModel @Inject constructor(
    private val signupLoginRepository: SignupLoginRepository
) : ViewModel() {

    private val _signOutResponse = MutableLiveData<Resource<Boolean>?>()
    val signOutResponse: MutableLiveData<Resource<Boolean>?> = _signOutResponse


    @OptIn(ExperimentalCoroutinesApi::class)
    fun signOut() {
        _signOutResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            signupLoginRepository.signOut().collect {
                when {
                    it.isSuccess -> {
                        _signOutResponse.postValue(it.getOrNull())
                    }

                    it.isFailure -> {
                        _signOutResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }


}