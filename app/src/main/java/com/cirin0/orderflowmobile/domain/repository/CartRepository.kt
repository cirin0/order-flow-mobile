package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.cart.CartItem
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import com.cirin0.orderflowmobile.util.Resource

interface CartRepository {
    suspend fun getCartByUserId(userId: String): Resource<CartResponse>
    suspend fun addItemToCart(
        cartId: String,
        item: CartItem,
    ): Resource<CartResponse>

    suspend fun updateItemInCart(
        cartId: String,
        itemId: String,
        quantity: Int,
    ): Resource<CartResponse>

    suspend fun removeItemFromCart(
        cartId: String,
        itemId: String,
    ): Resource<CartResponse>

    suspend fun clearCart(cartId: String): Resource<CartResponse>
}
