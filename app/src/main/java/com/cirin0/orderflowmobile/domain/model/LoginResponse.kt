package com.cirin0.orderflowmobile.domain.model

data class LoginResponse(
    val token: String,
    val userId: String,
    val name: String,
    val email: String
) 