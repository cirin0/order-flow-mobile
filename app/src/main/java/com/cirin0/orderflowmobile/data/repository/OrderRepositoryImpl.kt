package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.OrderApi
import com.cirin0.orderflowmobile.domain.model.order.OrderRequest
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import com.cirin0.orderflowmobile.domain.repository.OrderRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: OrderApi,
) : OrderRepository {
    override suspend fun getOrdersByUserId(userId: String): Resource<List<OrderResponse>> {
        return try {
            val response = api.getOrdersByUserId(userId)
            if (response.isSuccessful) {
                response.body()?.let { orderResponse ->
                    Resource.Success(orderResponse)
                } ?: Resource.Error("Orders not found")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun createOrder(
        request: OrderRequest
    ): Resource<OrderResponse> {
        return try {
            val response = api.createOrder(request.userEmail, request)
            if (response.isSuccessful) {
                response.body()?.let { orderResponse ->
                    Resource.Success(orderResponse)
                } ?: Resource.Error("Failed to create order")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getOrderById(id: String): Resource<OrderResponse> {
        return try {
            val response = api.getOrderById(id)
            if (response.isSuccessful) {
                response.body()?.let { orderResponse ->
                    Resource.Success(orderResponse)
                } ?: Resource.Error("Order not found")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun completeOrder(id: String): Resource<OrderResponse> {
        return try {
            val response = api.completeOrder(id)
            if (response.isSuccessful) {
                response.body()?.let { orderResponse ->
                    Resource.Success(orderResponse)
                } ?: Resource.Error("Failed to complete order")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun cancelOrder(id: String): Resource<OrderResponse> {
        return try {
            val response = api.cancelOrder(id)
            if (response.isSuccessful) {
                response.body()?.let { orderResponse ->
                    Resource.Success(orderResponse)
                } ?: Resource.Error("Failed to cancel order")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
