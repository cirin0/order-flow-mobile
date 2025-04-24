package com.cirin0.orderflowmobile.domain.model

data class SearchResult(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList()
)
