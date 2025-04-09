package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.CategoryApi
import com.cirin0.orderflowmobile.domain.model.Category
import com.cirin0.orderflowmobile.domain.repository.CategoryRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApi
) : CategoryRepository {
    override suspend fun getCategories(): Resource<List<Category>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful) {
                val categoriesDto = response.body()
                val categories = categoriesDto?.map { dto ->
                    Category(
                        id = dto.id,
                        name = dto.name,
                        imageUrl = dto.imageUrl
                    )
                } ?: emptyList<Category>()
                Resource.Success(categories)
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getCategoryById(id: String): Resource<Category> {
        return try {
            val response = api.getCategoryById(id)
            if (response.isSuccessful) {
                response.body()?.let { categoryDto ->
                    val category = Category(
                        id = categoryDto.id,
                        name = categoryDto.name,
                        imageUrl = categoryDto.imageUrl
                    )
                    Resource.Success(category)
                } ?: Resource.Error("Category not found")
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}