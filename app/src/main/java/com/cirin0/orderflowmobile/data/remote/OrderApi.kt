package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.order.OrderRequest
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {
    @GET("api/orders/user/id/{userId}")
    suspend fun getOrdersByUserId(@Path("userId") userId: String): Response<List<OrderResponse>>

    @POST("api/orders")
    suspend fun createOrder(
        @Query("userEmail") userEmail: String,
        @Body orderRequest: OrderRequest
    ): Response<OrderResponse>

    @GET("api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<OrderResponse>

    @PATCH("api/orders/{id}/complete")
    suspend fun completeOrder(@Path("id") id: String): Response<OrderResponse>

    @PATCH("api/orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String): Response<OrderResponse>
}
