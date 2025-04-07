package com.cirin0.orderflowmobile.presentation.product

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.usecase.GetProductByIdUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
) : ViewModel() {

    private val _product = mutableStateOf<Resource<ProductDetails>>(Resource.Loading())

    val product: State<Resource<ProductDetails>> = _product

    fun loadProduct(id: Int?) {
        viewModelScope.launch {
            _product.value = getProductByIdUseCase(id)
        }
    }
}