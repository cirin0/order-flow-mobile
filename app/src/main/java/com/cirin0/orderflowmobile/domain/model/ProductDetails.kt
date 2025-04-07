package com.cirin0.orderflowmobile.domain.model

data class ProductDetails(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val description: String,
    val price: Double,
    val categoryName: String,
    val averageRating: Double
)
