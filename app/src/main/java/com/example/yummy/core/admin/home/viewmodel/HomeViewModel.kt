package com.example.yummy.core.admin.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.AddProductsRepository
import com.example.yummy.data.repository.model.Product
import com.example.yummy.data.repository.model.local.ProductsEntity
import com.example.yummy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val addProductsRepository: AddProductsRepository
) : ViewModel() {

    private val _getAllAvailableProductsResponse = MutableLiveData<Resource<List<Product>>?>()
    val getAllAvailableProductsResponse: MutableLiveData<Resource<List<Product>>?> = _getAllAvailableProductsResponse


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllProducts() {
        _getAllAvailableProductsResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            addProductsRepository.getAllProductsFromFireStore().collect {
                when {
                    it.isSuccess -> {
                        _getAllAvailableProductsResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        _getAllAvailableProductsResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }
}