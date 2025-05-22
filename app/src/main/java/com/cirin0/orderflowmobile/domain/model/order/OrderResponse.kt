package com.cirin0.orderflowmobile.domain.model.order

import com.cirin0.orderflowmobile.domain.model.user.AddressItem

data class OrderResponse(
    val id: String,
    val userId: String,
    val userEmail: String,
    val userFirstName: String,
    val userLastName: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val orderDate: String,
    val status: String,
    val statusDescription: String,
    val orderNumber: String,
    val deliveryAddress: AddressItem,
)
