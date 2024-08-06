package com.example.yummy.core.admin.home

import androidx.lifecycle.ViewModel
import com.example.yummy.data.repository.AddProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val addProductsRepository: AddProductsRepository
) : ViewModel() {
}