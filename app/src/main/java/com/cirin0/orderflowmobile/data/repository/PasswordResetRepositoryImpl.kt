package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.PasswordResetApi
import com.cirin0.orderflowmobile.domain.model.password.EmailRequest
import com.cirin0.orderflowmobile.domain.model.password.ResetPassword
import com.cirin0.orderflowmobile.domain.model.password.ValidatePasswordCode
import com.cirin0.orderflowmobile.domain.repository.PasswordResetRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class PasswordResetRepositoryImpl @Inject constructor(
    private val api: PasswordResetApi
) : PasswordResetRepository {
    override suspend fun sendPasswordResetEmail(email: String): Resource<String> {
        return try {
            val emailRequest = EmailRequest(email)
            val response = api.sendPasswordResetEmail(emailRequest)
            if (response.isSuccessful) {
                val responseBody = response.body()
                Resource.Success(responseBody ?: "Password reset email sent")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Resource.Error(errorBody)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun validatePasswordResetCode(validatePasswordCode: ValidatePasswordCode): Resource<Boolean> {
        return try {
            val response = api.validatePasswordResetCode(validatePasswordCode)
            if (response.isSuccessful) {
                val isValid = response.body() == true
                Resource.Success(isValid)
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun resetPassword(resetPassword: ResetPassword): Resource<String> {
        return try {
            val response = api.resetPassword(resetPassword)
            if (response.isSuccessful) {
                val responseBody = response.body()
                Resource.Success(responseBody)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Resource.Error(errorBody)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
