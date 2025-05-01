package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.user.AddressItem
import com.cirin0.orderflowmobile.domain.model.user.AddressRequest
import com.cirin0.orderflowmobile.domain.model.user.UserResponse
import com.cirin0.orderflowmobile.util.Resource

interface UserRepository {
    suspend fun getUserByEmail(email: String): Resource<UserResponse>
    suspend fun updateUser(
        userId: String,
        userData: UserResponse
    ): Resource<UserResponse>

    suspend fun addAddress(
        userId: String,
        addressRequest: AddressRequest
    ): Resource<AddressItem>

    suspend fun updateAddress(
        userId: String,
        addressRequest: AddressRequest
    ): Resource<AddressItem>

}
