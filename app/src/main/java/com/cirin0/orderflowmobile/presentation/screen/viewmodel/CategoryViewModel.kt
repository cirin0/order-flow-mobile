package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.usecase.AddFavoriteUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetProductByCategoryNameUseCase
import com.cirin0.orderflowmobile.domain.usecase.IsFavoriteUseCase
import com.cirin0.orderflowmobile.domain.usecase.RemoveFavoriteUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getProductByCategoryNameUseCase: GetProductByCategoryNameUseCase,
    savedStateHandle: SavedStateHandle,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {
    private val _category = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val category: MutableStateFlow<Resource<List<Product>>> = _category

    private val _productFavoriteStatus = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val productFavoriteStatus: StateFlow<Map<Int, Boolean>> = _productFavoriteStatus

    init {
        savedStateHandle.get<String>("categoryName")?.let { categoryName ->
            getCategory(categoryName)
        }
    }

    fun getCategory(name: String) {
        viewModelScope.launch {
            _category.value = Resource.Loading()
            val result = getProductByCategoryNameUseCase(name)
            _category.value = result

            if (result is Resource.Success) {
                result.data?.let { products ->
                    checkFavoriteStatusForProducts(products)
                }
            }
        }
    }

    fun refreshData(name: String) {
        getCategory(name)
    }

    private fun checkFavoriteStatusForProducts(products: List<Product>) {
        viewModelScope.launch {
            products.forEach { product ->
                viewModelScope.launch {
                    isFavoriteUseCase(product.id).collect { isFavorite ->
                        val updatedStatus = _productFavoriteStatus.value.toMutableMap()
                        updatedStatus[product.id] = isFavorite
                        _productFavoriteStatus.value = updatedStatus
                    }
                }
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
