package com.example.yummy.core.user.productdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.CartItemRepository
import com.example.yummy.data.repository.model.CartItem
import com.example.yummy.data.repository.model.Product
import com.example.yummy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository
): ViewModel() {

    private val _addProductToCartResponse = MutableLiveData<Resource<Boolean>?>()
    val addProductToCartResponse: MutableLiveData<Resource<Boolean>?> = _addProductToCartResponse


    @OptIn(ExperimentalCoroutinesApi::class)
    fun addProductsToCart(product: Product, quantity: String) {
        _addProductToCartResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            cartItemRepository.addCartItemToRealtimeDatabase(product, quantity).collect {
                when {
                    it.isSuccess -> {
                        _addProductToCartResponse.postValue(it.getOrNull())
                    }
                    it.isFailure -> {
                        _addProductToCartResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }
}