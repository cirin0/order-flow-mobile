package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.review.ReviewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewApi {
    @GET("api/reviews/product/{productId}")
    suspend fun getReviews(@Path("productId") productId: String): Response<List<ReviewResponse>>
}
