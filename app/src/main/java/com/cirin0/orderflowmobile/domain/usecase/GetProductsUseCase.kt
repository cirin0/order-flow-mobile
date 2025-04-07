package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.repository.ProductRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): Resource<List<Product>> {
        return try {
            val products = repository.getProducts()
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}