package com.cirin0.orderflowmobile.domain.model.review

data class ReviewResponse(
    val id: String,
    val content: String,
    val rating: Int,
    val productId: String,
    val userFirst_name: String,
    val userLast_name: String,
    val createdAt: String,
    val updatedAt: String,
)
