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
    override suspend fun getProducts(): Resource<List<Product>> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                val productsDto = response.body()
                val products = productsDto?.map { dto ->
                    Product(
                        id = dto.id,
                        name = dto.name,
                        imageUrl = dto.imageUrl,
                        price = dto.price
                    )
                } ?: emptyList<Product>()
                Resource.Success(products)
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getProductById(id: String): Resource<ProductDetails> {
        return try {
            val response = api.getProductById(id)
            if (response.isSuccessful) {
                response.body()?.let { productDetailsDto ->
                    val productDetails = ProductDetails(
                        id = productDetailsDto.id,
                        name = productDetailsDto.name,
                        imageUrl = productDetailsDto.imageUrl,
                        price = productDetailsDto.price,
                        description = productDetailsDto.description,
                        categoryId = productDetailsDto.categoryId,
                        categoryName = productDetailsDto.categoryName,
                        averageRating = productDetailsDto.averageRating,
                        stock = productDetailsDto.stock,
                        createdAt = productDetailsDto.createdAt,
                    )
                    Resource.Success(productDetails)
                } ?: Resource.Error("Product not found")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getProductsByCategoryName(categoryName: String): Resource<List<Product>> {
        return try {
            val response = api.getProductsByCategoryName(categoryName)
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
