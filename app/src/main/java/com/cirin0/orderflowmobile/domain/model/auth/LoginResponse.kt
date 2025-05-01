package com.cirin0.orderflowmobile.domain.model.auth

data class LoginResponse(
    val token: String,
    val userId: String,
    val email: String
)
