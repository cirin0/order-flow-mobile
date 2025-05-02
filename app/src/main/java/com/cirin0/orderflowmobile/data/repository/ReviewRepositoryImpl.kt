package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.ReviewApi
import com.cirin0.orderflowmobile.domain.model.review.ReviewResponse
import com.cirin0.orderflowmobile.domain.repository.ReviewRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    val api: ReviewApi
) : ReviewRepository {
    override suspend fun getReviewsByProductId(productId: String): Resource<List<ReviewResponse>> {
        return try {
            val response = api.getReviews(productId)
            if (response.isSuccessful) {
                response.body()?.let { reviews ->
                    Resource.Success(reviews)
                } ?: Resource.Success(emptyList<ReviewResponse>())
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
