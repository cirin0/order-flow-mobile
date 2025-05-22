package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import com.cirin0.orderflowmobile.domain.model.order.OrderAddress
import com.cirin0.orderflowmobile.domain.model.order.OrderRequest
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import com.cirin0.orderflowmobile.domain.repository.CartRepository
import com.cirin0.orderflowmobile.domain.repository.OrderRepository
import com.cirin0.orderflowmobile.domain.repository.UserRepository
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
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _orderState = MutableStateFlow<Resource<OrderResponse>>(Resource.Loading())
    val orderState: StateFlow<Resource<OrderResponse>> = _orderState.asStateFlow()

    private val _cartState = MutableStateFlow<Resource<CartResponse>>(Resource.Loading())
    val cartState: StateFlow<Resource<CartResponse>> = _cartState.asStateFlow()

    private val _processingOrder = MutableStateFlow(false)
    val processingOrder: StateFlow<Boolean> = _processingOrder.asStateFlow()

    init {
        loadCart()
    }

    private suspend fun getUserId(): String {
        return tokenManager.userId.firstOrNull() ?: ""
    }

    private suspend fun getUserEmail(): String {
        return tokenManager.email.firstOrNull() ?: ""
    }

    private fun loadCart() {
        viewModelScope.launch {
            val userId = getUserId()
            if (userId.isNotBlank()) {
                _cartState.value = Resource.Loading()
                try {
                    val result = cartRepository.getCartByUserId(userId)
                    if (result is Resource.Error) {
                        _cartState.value = Resource.Error(result.message ?: "Error fetching cart")
                    } else {
                        _cartState.value = result
                    }
                } catch (e: Exception) {
                    _cartState.value = Resource.Error("Error fetching cart: ${e.message}")
                }
            } else {
                _cartState.value = Resource.Error("User ID is empty")
            }
        }
    }

    fun createOrderWithNewAddress(address: OrderAddress) {
        viewModelScope.launch {
            _processingOrder.value = true
            val cartData = (_cartState.value as? Resource.Success)?.data
            val userId = getUserId()
            val email = getUserEmail()

            if (cartData == null) {
                _orderState.value = Resource.Error("Cart is empty")
                _processingOrder.value = false
                return@launch
            }
            val createOrderRequest = OrderRequest(
                userId = userId,
                userEmail = email,
                deliveryAddress = address
            )

            try {
                val result = orderRepository.createOrder(createOrderRequest)
                _orderState.value = result

                if (result is Resource.Success) {
                    cartRepository.clearCart(cartData.id)
                }
            } catch (e: Exception) {
                _orderState.value = Resource.Error("Error creating order: ${e.message}")
            } finally {
                _processingOrder.value = false
            }
        }
    }

    fun createOrder() {
        viewModelScope.launch {
            _processingOrder.value = true
            val cartData = (_cartState.value as? Resource.Success)?.data
            val userId = getUserId()
            val email = getUserEmail()

            if (cartData == null) {
                _orderState.value = Resource.Error("Cart is empty")
                _processingOrder.value = false
                return@launch
            }
            val userInfo = userRepository.getUserByEmail(email)
            val address =
                if (userInfo is Resource.Success && userInfo.data != null && userInfo.data.address != null) {
                    val userAddress = userInfo.data.address
                    OrderAddress(
                        region = userAddress.region,
                        city = userAddress.city,
                        area = userAddress.area,
                        street = userAddress.street,
                        house = userAddress.house,
                        apartment = userAddress.apartment
                    )
                } else {
                    _orderState.value = Resource.Error("Адреса не знайдена")
                    _processingOrder.value = false
                    return@launch
                }

            val createOrderRequest = OrderRequest(
                userId = userId,
                userEmail = email,
                deliveryAddress = address
            )

            try {
                val result = orderRepository.createOrder(createOrderRequest)
                _orderState.value = result

                if (result is Resource.Success) {
                    cartRepository.clearCart(cartData.id)
                }
            } catch (e: Exception) {
                _orderState.value = Resource.Error("Error creating order: ${e.message}")
            } finally {
                _processingOrder.value = false
            }
        }
    }
}
