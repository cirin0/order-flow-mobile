package com.cirin0.orderflowmobile.domain.model.user

data class AddressRequest(
    val region: String,
    val city: String,
    val area: String,
    val street: String,
    val apartment: String,
    val house: String
)
