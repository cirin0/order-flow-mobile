package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.model.review.ReviewResponse
import com.cirin0.orderflowmobile.domain.usecase.AddFavoriteUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetProductByIdUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetReviewByProductIdUseCase
import com.cirin0.orderflowmobile.domain.usecase.IsFavoriteUseCase
import com.cirin0.orderflowmobile.domain.usecase.RemoveFavoriteUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val getReviewByProductIdUseCase: GetReviewByProductIdUseCase,
) : ViewModel() {

    private val _product = MutableStateFlow<Resource<ProductDetails>>(Resource.Loading())
    val product: StateFlow<Resource<ProductDetails>> = _product

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _reviews = MutableStateFlow<Resource<List<ReviewResponse>>>(Resource.Loading())
    val reviews: StateFlow<Resource<List<ReviewResponse>>> = _reviews.asStateFlow()

    fun getProduct(id: String) {
        viewModelScope.launch {
            _product.value = Resource.Loading()
            try {
                val result = getProductByIdUseCase(id)
                _product.value = result

                isFavoriteUseCase(result.data?.id ?: 0)
                    .catch { }
                    .collect { isFav ->
                        _isFavorite.value = isFav
                    }
            } catch (e: Exception) {
                _product.value = Resource.Error(message = e.message ?: "Unknown error")
            }
        }
    }

    fun refreshData(id: String) {
        getProduct(id)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val productValue = _product.value
            if (productValue is Resource.Success && productValue.data != null) {
                val productDetails = productValue.data
                if (_isFavorite.value) {
                    removeFavoriteUseCase(productDetails.id)
                    _isFavorite.value = false
                } else {
                    addFavoriteUseCase(productDetails)
                    _isFavorite.value = true
                }
            }
        }
    }

    fun getReviews(productId: String) {
        viewModelScope.launch {
            _reviews.value = Resource.Loading()
            try {
                val result = getReviewByProductIdUseCase(productId)
                _reviews.value = result
            } catch (e: Exception) {
                _reviews.value = Resource.Error(message = e.message ?: "Unknown error")
            }
        }
    }
}
