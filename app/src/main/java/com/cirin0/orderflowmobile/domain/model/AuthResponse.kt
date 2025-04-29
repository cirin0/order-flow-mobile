package com.cirin0.orderflowmobile.domain.model

data class AuthResponse(
    val userId: String,
    val email: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String,
    val expirationTime: Long,
    val refreshExpirationTime: Long
)
