package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.user.UserResponse
import com.cirin0.orderflowmobile.domain.repository.UserRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String): Resource<UserResponse> {
        return repository.getUserByEmail(email)
    }
}
