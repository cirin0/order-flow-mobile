package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.usecase.GetProductByIdUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _product = MutableStateFlow<Resource<ProductDetails>>(Resource.Loading())
    val product: StateFlow<Resource<ProductDetails>> = _product

    init {
        savedStateHandle.get<String>("productId")?.let { productId ->
            getProduct(productId)
        }
    }

    fun getProduct(id: String) {
        viewModelScope.launch {
            _product.value = Resource.Loading()
            _product.value = getProductByIdUseCase(id)
        }
    }
}