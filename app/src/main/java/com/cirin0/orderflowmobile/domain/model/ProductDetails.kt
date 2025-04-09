package com.cirin0.orderflowmobile.domain.model

data class ProductDetails(
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
