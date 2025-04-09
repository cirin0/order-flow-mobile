package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.Category
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApi {
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") categoryId: String): Response<Category>
}