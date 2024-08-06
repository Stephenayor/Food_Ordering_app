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

    private val _getProductsResponse = MutableLiveData<Resource<Product>?>()
    val getProductsResponse: MutableLiveData<Resource<Product>?> = _getProductsResponse



    @OptIn(ExperimentalCoroutinesApi::class)
    fun getProducts() {
        _getProductsResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            addProductsRepository.getProductsFromFireStoreDatabase().collect {
                when {
                    it.isSuccess -> {
                        _getProductsResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        _getProductsResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }
}