package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.Category
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.usecase.AddFavoriteUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetCategoriesUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetProductsUseCase
import com.cirin0.orderflowmobile.domain.usecase.IsFavoriteUseCase
import com.cirin0.orderflowmobile.domain.usecase.RemoveFavoriteUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val products: StateFlow<Resource<List<Product>>> = _products

    private val _categories = MutableStateFlow<Resource<List<Category>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<Category>>> = _categories

    private val _productFavoriteStatus = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val productFavoriteStatus: StateFlow<Map<Int, Boolean>> = _productFavoriteStatus

    init {
        getProducts()
        getCategories()
    }

    private fun getProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            val result = getProductsUseCase()
            _products.value = result
            if (result is Resource.Success) {
                result.data?.let { products ->
                    checkFavoriteStatusForProducts(products)
                }
            }
        }
    }

    fun refreshData() {
        getProducts()
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            _categories.value = Resource.Loading()
            _categories.value = getCategoriesUseCase()
        }
    }

    private fun checkFavoriteStatusForProducts(products: List<Product>) {
        viewModelScope.launch {
            val updatedStatus = mutableMapOf<Int, Boolean>()
            products.forEach { product ->
                viewModelScope.launch {
                    isFavoriteUseCase(product.id).collect { isFavorite ->
                        val updatedStatus = _productFavoriteStatus.value.toMutableMap()
                        updatedStatus[product.id] = isFavorite
                        _productFavoriteStatus.value = updatedStatus
                    }
                }
                _productFavoriteStatus.value = updatedStatus
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            val isFavorite = _productFavoriteStatus.value[product.id] ?: false
            if (isFavorite) {
                removeFavoriteUseCase(product.id)
            } else {
                addFavoriteUseCase(product)
            }
            val updatedStatus = _productFavoriteStatus.value.toMutableMap()
            updatedStatus[product.id] = !isFavorite
            _productFavoriteStatus.value = updatedStatus
        }
    }
}
