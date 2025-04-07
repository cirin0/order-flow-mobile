package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.util.Resource

interface ProductRepository {
    suspend fun getProductById(id: Int?): Resource<ProductDetails>
    suspend fun getProducts(): List<Product>
}