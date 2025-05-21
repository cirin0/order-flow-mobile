package com.cirin0.orderflowmobile.domain.model.order

data class OrderRequest(
    val userId: String,
    val userEmail: String,
    val deliveryAddress: OrderAddress,
)
