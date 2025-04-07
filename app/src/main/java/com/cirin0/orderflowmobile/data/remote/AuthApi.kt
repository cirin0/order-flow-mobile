package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.LoginRequest
import com.cirin0.orderflowmobile.domain.model.LoginResponse
import com.cirin0.orderflowmobile.domain.model.RegisterRequest
import com.cirin0.orderflowmobile.domain.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    
    @POST("api/authentication/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/authentication/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("api/authentication/logout")
    suspend fun logout(): Response<Unit>
} 