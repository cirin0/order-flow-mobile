package com.cirin0.orderflowmobile.domain.model.cart

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val price: Double,
    val stockQuantity: Int,
)
