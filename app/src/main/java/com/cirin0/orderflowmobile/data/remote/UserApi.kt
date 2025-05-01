package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.user.AddressItem
import com.cirin0.orderflowmobile.domain.model.user.AddressRequest
import com.cirin0.orderflowmobile.domain.model.user.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("api/users/email/{email}")
    suspend fun getUserByEmail(
        @Path("email")
        email: String
    ): Response<UserResponse>

    @PUT("api/users/email{email}")
    suspend fun updateUser(
        @Path("email") userId: String,
        @Body userData: UserResponse
    ): Response<UserResponse>

    @POST("api/users/{id}/address")
    suspend fun addAddress(
        @Path("id") userId: String,
        @Body addressRequest: AddressRequest
    ): Response<AddressItem>

    @PUT("api/users/{userId}/address/{addressId}")
    suspend fun updateAddress(
        @Path("userId") userId: String,
        @Body addressRequest: AddressRequest
    ): Response<AddressItem>
}
