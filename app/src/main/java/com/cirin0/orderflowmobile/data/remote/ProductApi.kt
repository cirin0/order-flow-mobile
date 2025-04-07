package com.cirin0.orderflowmobile.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int?): ProductResponse

    @GET("api/products")
    suspend fun getProducts(): List<ProductResponse>
}

data class ProductResponse(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val createdAt: String,
    val categoryId: Int,
    val categoryName: String,
    val averageRating: Double
)