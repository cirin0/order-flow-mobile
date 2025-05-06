package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.password.ResetPassword
import com.cirin0.orderflowmobile.domain.model.password.ValidatePasswordCode
import com.cirin0.orderflowmobile.util.Resource

interface PasswordResetRepository {
    suspend fun sendPasswordResetEmail(email: String): Resource<String>
    suspend fun validatePasswordResetCode(validatePasswordCode: ValidatePasswordCode): Resource<Boolean>
    suspend fun resetPassword(resetPassword: ResetPassword): Resource<String>
}
