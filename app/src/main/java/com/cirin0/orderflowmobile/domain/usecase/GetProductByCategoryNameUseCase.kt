package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.repository.ProductRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class GetProductByCategoryNameUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(name: String): Resource<List<Product>> {
        return repository.getProductsByCategoryName(name)
    }
}
