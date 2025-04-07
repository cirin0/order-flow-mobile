package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.repository.ProductRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Int?): Resource<ProductDetails> {
        return try {
            val product = repository.getProductById(id)
            Resource.Success(product.data)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}