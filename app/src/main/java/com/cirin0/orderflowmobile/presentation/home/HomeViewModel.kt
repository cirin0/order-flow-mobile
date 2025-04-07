package com.cirin0.orderflowmobile.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.usecase.GetProductsUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {

    private val _products = mutableStateOf<Resource<List<Product>>>(Resource.Loading())
    val products: State<Resource<List<Product>>> = _products

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _products.value = getProductsUseCase()
        }
    }
}