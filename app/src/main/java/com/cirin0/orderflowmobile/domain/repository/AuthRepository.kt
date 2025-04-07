package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.LoginRequest
import com.cirin0.orderflowmobile.domain.model.LoginResponse
import com.cirin0.orderflowmobile.domain.model.RegisterResponse
import com.cirin0.orderflowmobile.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<LoginResponse>
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<RegisterResponse>
} 