package com.cirin0.orderflowmobile.domain.model.auth

data class RegisterResponse(
    val token: String,
    val userId: String,
    val email: String,
)
