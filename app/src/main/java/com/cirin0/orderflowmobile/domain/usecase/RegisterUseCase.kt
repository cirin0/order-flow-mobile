package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.user.AuthResponse
import com.cirin0.orderflowmobile.domain.repository.AuthRepository
import com.cirin0.orderflowmobile.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        val result = repository.register(firstName, lastName, email, password)
        emit(result)
    }
}
