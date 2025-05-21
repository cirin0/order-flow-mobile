package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.order.OrderRequest
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import com.cirin0.orderflowmobile.util.Resource

interface OrderRepository {
    suspend fun getOrdersByUserId(userId: String): Resource<List<OrderResponse>>

    suspend fun createOrder(request: OrderRequest): Resource<OrderResponse>

    suspend fun getOrderById(id: String): Resource<OrderResponse>

    suspend fun completeOrder(id: String): Resource<OrderResponse>

    suspend fun cancelOrder(id: String): Resource<OrderResponse>
}
