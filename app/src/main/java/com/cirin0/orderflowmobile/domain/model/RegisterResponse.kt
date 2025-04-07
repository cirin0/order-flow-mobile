package com.cirin0.orderflowmobile.domain.model

data class RegisterResponse(
    val token: String,
    val userId: String,
    val email: String,
)
