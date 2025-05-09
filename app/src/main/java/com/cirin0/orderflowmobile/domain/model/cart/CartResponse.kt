package com.cirin0.orderflowmobile.domain.model.cart

data class CartResponse(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val totalPrice: Double,
)
