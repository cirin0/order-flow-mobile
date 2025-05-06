package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.password.EmailRequest
import com.cirin0.orderflowmobile.domain.model.password.ResetPassword
import com.cirin0.orderflowmobile.domain.model.password.ValidatePasswordCode
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PasswordResetApi {
    @POST("api/passwords/forgot")
    suspend fun sendPasswordResetEmail(
        @Body email: EmailRequest
    ): Response<String>

    @POST("api/passwords/validate-code")
    suspend fun validatePasswordResetCode(
        @Body validatePasswordCode: ValidatePasswordCode
    ): Response<Boolean>

    @POST("api/passwords/reset")
    suspend fun resetPassword(
        @Body resetPassword: ResetPassword
    ): Response<String>
}
