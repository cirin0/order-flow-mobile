package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.user.AuthResponse
import com.cirin0.orderflowmobile.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<AuthResponse>
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<AuthResponse>

    suspend fun refreshToken(): Resource<AuthResponse>
    suspend fun validateToken(): Resource<Boolean>
    suspend fun logout()
}
