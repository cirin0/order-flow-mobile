package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.util.Resource

interface ProductRepository {
    suspend fun getProducts(): Resource<List<Product>>
    suspend fun getProductById(id: String): Resource<ProductDetails>
    suspend fun getProductsByCategoryName(categoryName: String): Resource<List<Product>>
}
