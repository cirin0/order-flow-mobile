package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.review.ReviewResponse
import com.cirin0.orderflowmobile.domain.repository.ReviewRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class GetReviewByProductIdUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(productId: String): Resource<List<ReviewResponse>> {
        return repository.getReviewsByProductId(productId)
    }
}
