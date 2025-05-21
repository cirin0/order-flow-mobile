package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import com.cirin0.orderflowmobile.domain.repository.OrderRepository
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderState = MutableStateFlow<Resource<OrderResponse>>(Resource.Loading())
    val orderState: StateFlow<Resource<OrderResponse>> = _orderState.asStateFlow()

    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            _orderState.value = Resource.Loading()
            try {
                val result = orderRepository.getOrderById(orderId)
                _orderState.value = result
            } catch (e: Exception) {
                _orderState.value = Resource.Error("Failed to load order: ${e.message}")
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                val result = orderRepository.cancelOrder(orderId)
                if (result is Resource.Success) {
                    _orderState.value = result
                }
            } catch (e: Exception) {
                _orderState.value = Resource.Error("Failed to cancel order: ${e.message}")
            }
        }
    }
}
