package com.cirin0.orderflowmobile.domain.model.user

data class UserResponse(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val phone: String? = null,
    val address: AddressItem? = null
)
