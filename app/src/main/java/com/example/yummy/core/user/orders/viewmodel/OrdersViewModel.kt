package com.example.yummy.core.user.orders.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummy.data.repository.OrdersRepository
import com.example.yummy.data.repository.model.Orders
import com.example.yummy.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository
) : ViewModel() {

    private val _ordersResponse = MutableLiveData<Resource<List<Orders>>?>()
    val ordersResponse: MutableLiveData<Resource<List<Orders>>?> get() = _ordersResponse

    private val _filteredOrdersResponse = MutableLiveData<Resource<List<Orders>>?>()
    val filteredOrdersResponse: MutableLiveData<Resource<List<Orders>>?> get() = _filteredOrdersResponse

    private var lastVisibleDocument: DocumentSnapshot? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllOrders(isInitialLoad: Boolean = false) {

        viewModelScope.launch {
            if (isInitialLoad) lastVisibleDocument = null

            ordersRepository.getAllOrdersFromFireStore(lastVisibleDocument).collect {
                when {
                    it.isSuccess -> {
                        _ordersResponse.postValue(it.getOrNull())
                    }

                    it.isFailure -> {
                        _ordersResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }


    fun filterOrdersByDate(startDate: Long, endData: Long) {
        _filteredOrdersResponse.postValue(Resource.Loading())
        viewModelScope.launch {

            ordersRepository.getOrdersByDateRange(startDate, endData).collect {
                when {
                    it.isSuccess -> {
                        _filteredOrdersResponse.postValue(it.getOrNull())
                    }

                    it.isFailure -> {
                        _filteredOrdersResponse.postValue(it.getOrNull())
                    }
                }
            }
        }
    }
}
