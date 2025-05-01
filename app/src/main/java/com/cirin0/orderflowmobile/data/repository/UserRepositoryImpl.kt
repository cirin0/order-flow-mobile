package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.UserApi
import com.cirin0.orderflowmobile.domain.model.user.AddressItem
import com.cirin0.orderflowmobile.domain.model.user.AddressRequest
import com.cirin0.orderflowmobile.domain.model.user.UserResponse
import com.cirin0.orderflowmobile.domain.repository.UserRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {
    override suspend fun getUserByEmail(email: String): Resource<UserResponse> {
        return try {
            val response = api.getUserByEmail(email)
            if (response.isSuccessful) {
                val userResponse = response.body()
                if (userResponse != null) {
                    Resource.Success(userResponse)
                } else {
                    Resource.Error("User not found")
                }
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun updateUser(
        userId: String,
        userData: UserResponse
    ): Resource<UserResponse> {
        return try {
            TODO()
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun addAddress(
        userId: String,
        addressRequest: AddressRequest
    ): Resource<AddressItem> {
        TODO()
    }

    override suspend fun updateAddress(
        userId: String,
        addressRequest: AddressRequest
    ): Resource<AddressItem> {
        TODO()
    }
}
