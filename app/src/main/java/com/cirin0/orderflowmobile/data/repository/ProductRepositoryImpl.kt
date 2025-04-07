package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.ProductApi
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.repository.ProductRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi
) : ProductRepository {

    override suspend fun getProductById(id: Int?): Resource<ProductDetails> {
        return try {
            val response = api.getProductById(id)
            val product = ProductDetails(
                response.id,
                response.name,
                response.imageUrl,
                response.description,
                response.price,
                response.categoryName,
                response.averageRating
            )
            Resource.Success(product)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getProducts(): List<Product> {
        return api.getProducts().map {
            Product(it.id, it.name, it.imageUrl, it.price)
        }
    }
}