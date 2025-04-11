package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<ProductDetails>

    @GET("api/products/category/name/{name}")
    suspend fun getProductsByCategoryName(@Path("name") categoryName: String): Response<List<Product>>
}
