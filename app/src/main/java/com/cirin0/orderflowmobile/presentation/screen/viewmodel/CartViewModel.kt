package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import com.cirin0.orderflowmobile.domain.repository.CartRepository
import com.cirin0.orderflowmobile.util.Resource
import com.cirin0.orderflowmobile.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _cart = MutableStateFlow<Resource<CartResponse>>(Resource.Loading())
    val cart: StateFlow<Resource<CartResponse>> = _cart.asStateFlow()

    init {
        loadCart()
    }

    private suspend fun getUserId(): String {
        return tokenManager.userId.firstOrNull() ?: ""
    }

    fun loadCart() {
        viewModelScope.launch {
            val userId = getUserId()
            if (userId.isNotBlank()) {
                _cart.value = Resource.Loading()
                try {
                    val result = cartRepository.getCartByUserId(userId)
                    if (result is Resource.Error) {
                        _cart.value = Resource.Error("Failed to load cart")
                    } else {
                        _cart.value = result
                    }
                } catch (e: Exception) {
                    _cart.value = Resource.Error(message = e.message ?: "Unknown error")
                }
            } else {
                _cart.value = Resource.Error("User not logged in")
            }
        }
    }

    fun updateItemQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            val cartData = (_cart.value as? Resource.Success)?.data
            val cartId = cartData?.id ?: return@launch
            val result = cartRepository.updateItemInCart(cartId, itemId, quantity)
            if (result is Resource.Success) {
                _cart.value = result
            } else {
                _cart.value = Resource.Error("Failed to update item in cart")
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            val cartData = (_cart.value as? Resource.Success)?.data
            val cartId = cartData?.id ?: return@launch

            val result = cartRepository.removeItemFromCart(cartId, itemId)
            if (result is Resource.Success) {
                _cart.value = result
            } else {
                _cart.value = Resource.Error("Failed to remove item from cart")
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            val cartData = (_cart.value as? Resource.Success)?.data
            val cartId = cartData?.id ?: return@launch

            val result = cartRepository.clearCart(cartId)
            if (result is Resource.Success) {
                _cart.value = result
            } else {
                _cart.value = Resource.Error("Failed to clear cart")
            }
        }
    }
}


