package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.Category
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.usecase.GetCategoriesUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetProductsUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val products: StateFlow<Resource<List<Product>>> = _products

    private val _categories = MutableStateFlow<Resource<List<Category>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<Category>>> = _categories

    init {
        getProducts()
        getCategories()
    }

    private fun getProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            _products.value = getProductsUseCase()
        }
    }


    private fun getCategories() {
        viewModelScope.launch {
            _categories.value = Resource.Loading()
            _categories.value = getCategoriesUseCase()
        }
    }

    fun refreshData() {
        getProducts()
        getCategories()
    }
}
