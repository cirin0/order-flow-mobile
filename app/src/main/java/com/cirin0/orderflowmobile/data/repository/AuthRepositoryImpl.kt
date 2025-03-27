package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.AuthApi
import com.cirin0.orderflowmobile.domain.model.LoginRequest
import com.cirin0.orderflowmobile.domain.model.LoginResponse
import com.cirin0.orderflowmobile.domain.repository.AuthRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = api.login(request)
            
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Помилка авторизації: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Не вдалося з'єднатися з сервером: ${e.localizedMessage}")
        }
    }
} 