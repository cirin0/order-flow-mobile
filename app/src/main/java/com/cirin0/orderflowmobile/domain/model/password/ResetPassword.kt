package com.cirin0.orderflowmobile.domain.model.password

data class ResetPassword(
    val email: String,
    val code: String,
    val newPassword: String,
    val confirmPassword: String
)
