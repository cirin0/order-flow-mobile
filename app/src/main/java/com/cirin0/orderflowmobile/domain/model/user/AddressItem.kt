package com.cirin0.orderflowmobile.domain.model.user

data class AddressItem(
    val id: Int,
    val region: String,
    val city: String,
    val area: String,
    val street: String,
    val apartment: String,
    val house: String
)
