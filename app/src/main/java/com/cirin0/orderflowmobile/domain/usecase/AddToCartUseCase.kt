package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.model.cart.CartItem
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import com.cirin0.orderflowmobile.domain.repository.CartRepository
import com.cirin0.orderflowmobile.util.Resource
import com.cirin0.orderflowmobile.util.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(
        product: ProductDetails,
        quantity: Int = 1
    ): Resource<CartResponse> {
        val userId = tokenManager.userId.firstOrNull() ?: ""
        val cartResult = cartRepository.getCartByUserId(userId)
        return when (cartResult) {
            is Resource.Success -> {
                val cartId = cartResult.data?.id ?: ""
                val cartItem = CartItem(
                    id = cartId,
                    productId = product.id.toString(),
                    productName = product.name,
                    productImageUrl = product.imageUrl,
                    quantity = quantity,
                    price = product.price,
                    stockQuantity = product.stock
                )
                cartRepository.addItemToCart(cartId, cartItem)
            }

            else -> Resource.Error(cartResult.message ?: "Failed to load cart")
        }
    }
}
