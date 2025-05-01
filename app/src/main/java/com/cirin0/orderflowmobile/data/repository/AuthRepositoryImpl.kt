package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.AuthApi
import com.cirin0.orderflowmobile.domain.model.auth.LoginRequest
import com.cirin0.orderflowmobile.domain.model.auth.RefreshTokenRequest
import com.cirin0.orderflowmobile.domain.model.auth.RegisterRequest
import com.cirin0.orderflowmobile.domain.model.user.AuthResponse
import com.cirin0.orderflowmobile.domain.repository.AuthRepository
import com.cirin0.orderflowmobile.util.Resource
import com.cirin0.orderflowmobile.util.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = api.login(request)

            if (response.isSuccessful) {
                Resource.Success(response.body()?.let {
                    tokenManager.saveAuthData(it)
                    it
                })
            } else {
                Resource.Error("Login failed: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Resource.Error("Could not connect to the server: ${e.localizedMessage}")
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<AuthResponse> {
        return try {
            val request = RegisterRequest(
                firstName,
                lastName,
                email,
                password
            )
            val response = api.register(request)
            if (response.isSuccessful) {
                Resource.Success(response.body()?.let {
                    tokenManager.saveAuthData(it)
                    it
                })
            } else {
                Resource.Error("Registration failed: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Resource.Error("Could not connect to the server: ${e.localizedMessage}")
        }
    }

    override suspend fun refreshToken(): Resource<AuthResponse> {
        return try {
            val currentRefreshToken = tokenManager.refreshToken.first()
            if (currentRefreshToken.isEmpty()) {
                return Resource.Error("No refresh token available")
            }
            val response = api.refreshToken(RefreshTokenRequest(currentRefreshToken))
            if (response.isSuccessful) {
                Resource.Success(response.body()?.let {
                    tokenManager.saveAuthData(it)
                    it
                })
            } else {
                Resource.Error("Token refresh failed: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Resource.Error("Could not connect to the server: ${e.localizedMessage}")
        }
    }

    override suspend fun validateToken(): Resource<Boolean> {
        return try {
            val currentAccessToken = tokenManager.accessToken.first()
            if (currentAccessToken.isEmpty()) {
                return Resource.Error("No access token available")
            }
            val response = api.validateToken(currentAccessToken)
            if (response.isSuccessful) {
                Resource.Success(response.body() == true)
            } else {
                Resource.Error("Token validation failed: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Resource.Error("Could not connect to the server: ${e.localizedMessage}")
        }
    }

    override suspend fun logout() {
        tokenManager.clearAuthData()
    }

    fun isLoggedIn() = tokenManager.accessToken.map { it.isNotEmpty() }
}
