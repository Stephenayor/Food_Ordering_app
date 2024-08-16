package com.example.yummy.core.admin.dashboard._admin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.AddProductsRepository
import com.example.yummy.data.repository.model.Product
import com.example.yummy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val addProductsRepository: AddProductsRepository
) : ViewModel() {

    private val _getLatestProductResponse = MutableLiveData<Resource<Product>?>()
    val getLatestProductResponse: MutableLiveData<Resource<Product>?> = _getLatestProductResponse



    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLatestProduct() {
        _getLatestProductResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            addProductsRepository.getLatestProductsFromFireStoreDatabase().collect {
                when {
                    it.isSuccess -> {
                        _getLatestProductResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        _getLatestProductResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }
}