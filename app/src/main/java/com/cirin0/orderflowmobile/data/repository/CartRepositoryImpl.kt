package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.CartApi
import com.cirin0.orderflowmobile.domain.model.cart.CartItem
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import com.cirin0.orderflowmobile.domain.repository.CartRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val api: CartApi
) : CartRepository {
    override suspend fun getCartByUserId(userId: String): Resource<CartResponse> {
        return try {
            val response = api.getCartByUserId(userId)
            if (response.isSuccessful) {
                response.body()?.let { cartResponse ->
                    Resource.Success(cartResponse)
                } ?: Resource.Error("Cart not found")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun addItemToCart(
        cartId: String,
        item: CartItem
    ): Resource<CartResponse> {
        return try {
            val response = api.addItemToCart(cartId, item)
            if (response.isSuccessful) {
                response.body()?.let { cartResponse ->
                    Resource.Success(cartResponse)
                } ?: Resource.Error("Failed to add item to cart")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun updateItemInCart(
        cartId: String,
        itemId: String,
        quantity: Int
    ): Resource<CartResponse> {
        return try {
            val response = api.updateItemInCart(cartId, itemId, quantity)
            if (response.isSuccessful) {
                response.body()?.let { cartResponse ->
                    Resource.Success(cartResponse)
                } ?: Resource.Error("Failed to update item in cart")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun removeItemFromCart(
        cartId: String,
        itemId: String
    ): Resource<CartResponse> {
        return try {
            val response = api.removeItemFromCart(cartId, itemId)
            if (response.isSuccessful) {
                response.body()?.let { cartResponse ->
                    Resource.Success(cartResponse)
                } ?: Resource.Error("Failed to remove item from cart")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun clearCart(cartId: String): Resource<CartResponse> {
        return try {
            val response = api.clearCart(cartId)
            if (response.isSuccessful) {
                response.body()?.let { cartResponse ->
                    Resource.Success(cartResponse)
                } ?: Resource.Error("Failed to clear cart")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
