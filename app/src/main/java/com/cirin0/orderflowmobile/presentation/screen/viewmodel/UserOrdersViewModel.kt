package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import com.cirin0.orderflowmobile.domain.repository.OrderRepository
import com.cirin0.orderflowmobile.util.Resource
import com.cirin0.orderflowmobile.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _orders = MutableStateFlow<Resource<List<OrderResponse>>>(Resource.Loading())
    val orders: StateFlow<Resource<List<OrderResponse>>> = _orders.asStateFlow()

    init {
        viewModelScope.launch {
            delay(300)
            loadOrder()
        }
    }

    private suspend fun getUserId(): String {
        return tokenManager.userId.firstOrNull() ?: ""
    }

    fun loadOrder() {
        viewModelScope.launch {
            val userId = getUserId()
            if (userId.isNotBlank()) {
                _orders.value = Resource.Loading()
                try {
                    val result = orderRepository.getOrdersByUserId(userId)
                    if (result is Resource.Error) {
                        _orders.value = Resource.Error(result.message ?: "Error fetching orders")
                    } else {
                        _orders.value = result
                    }
                } catch (e: Exception) {
                    _orders.value = Resource.Error("Error fetching orders: ${e.message}")
                }
            } else {
                _orders.value = Resource.Error("User ID is empty")
            }
        }
    }
}
