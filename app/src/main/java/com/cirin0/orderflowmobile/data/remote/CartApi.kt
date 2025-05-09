package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.cart.CartItem
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartApi {
    @GET("api/carts/{userId}")
    suspend fun getCartByUserId(@Path("userId") userId: String): Response<CartResponse>

    @POST("api/carts/{cartId}/items")
    suspend fun addItemToCart(
        @Path("cartId") cartId: String,
        @Body item: CartItem
    ): Response<CartResponse>

    @PUT("api/carts/{cartId}/items/{itemId}")
    suspend fun updateItemInCart(
        @Path("cartId") cartId: String,
        @Path("itemId") itemId: String,
        @Query("quantity") quantity: Int
    ): Response<CartResponse>

    @DELETE("api/carts/{cartId}/items/{itemId}")
    suspend fun removeItemFromCart(
        @Path("cartId") cartId: String,
        @Path("itemId") itemId: String
    ): Response<CartResponse>

    @DELETE("api/carts/{cartId}/clear")
    suspend fun clearCart(@Path("cartId") cartId: String): Response<CartResponse>
}
