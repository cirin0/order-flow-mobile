package com.cirin0.orderflowmobile.domain.model.auth

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
)
