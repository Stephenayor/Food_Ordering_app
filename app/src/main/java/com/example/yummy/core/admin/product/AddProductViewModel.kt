package com.example.yummy.core.admin.product

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.AddProductsRepository
import com.example.yummy.utils.Resource
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val addProductsRepository: AddProductsRepository
) : ViewModel(){

    private val _cloudStorageResponse = MutableLiveData<Resource<Uri>?>()
    val cloudStorageResponse: MutableLiveData<Resource<Uri>?> = _cloudStorageResponse

    private val _addProductToFireStoreResponse = MutableLiveData<Resource<Boolean>?>()
    val addProductToFireStoreResponse: MutableLiveData<Resource<Boolean>?> = _addProductToFireStoreResponse



    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadImageToCloudStorage(imageUri: Uri) {
        _cloudStorageResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            addProductsRepository.addImageToCloudStorage(imageUri).collect {
                when {
                    it.isSuccess -> {
                        _cloudStorageResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        _cloudStorageResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadProductsOnFireStore(imageUri: Uri, productName: String, productPrice: Double) {
        _addProductToFireStoreResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            addProductsRepository.addProductsToFireStoreDatabase(imageUri, productName, productPrice).collect {
                when {
                    it.isSuccess -> {
                        _addProductToFireStoreResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        _addProductToFireStoreResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }
}