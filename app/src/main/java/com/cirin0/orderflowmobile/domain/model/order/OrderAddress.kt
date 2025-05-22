package com.cirin0.orderflowmobile.domain.model.order

data class OrderAddress(
    val region: String,
    val city: String,
    val area: String,
    val street: String,
    val house: String,
    val apartment: String,
)
