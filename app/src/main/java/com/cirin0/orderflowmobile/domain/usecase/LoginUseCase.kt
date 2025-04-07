package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.LoginResponse
import com.cirin0.orderflowmobile.domain.repository.AuthRepository
import com.cirin0.orderflowmobile.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        val result = repository.login(email, password)
        emit(result)
    }
} 