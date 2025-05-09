package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.review.ReviewResponse
import com.cirin0.orderflowmobile.util.Resource

interface ReviewRepository {
    suspend fun getReviewsByProductId(productId: String): Resource<List<ReviewResponse>>
}
