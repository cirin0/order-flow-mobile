package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.AuthResponse
import com.cirin0.orderflowmobile.domain.model.LoginRequest
import com.cirin0.orderflowmobile.domain.model.RefreshTokenRequest
import com.cirin0.orderflowmobile.domain.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/refresh-token")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<AuthResponse>

    @POST("api/auth/validate")
    suspend fun validateToken(
        @Body accessToken: String
    ): Response<Boolean>
}
