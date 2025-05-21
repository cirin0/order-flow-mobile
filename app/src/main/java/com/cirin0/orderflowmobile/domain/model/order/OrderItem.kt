package com.cirin0.orderflowmobile.domain.model.order

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
)
