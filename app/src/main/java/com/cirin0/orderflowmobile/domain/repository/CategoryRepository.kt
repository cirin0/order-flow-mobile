package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.Category
import com.cirin0.orderflowmobile.util.Resource

interface CategoryRepository {
    suspend fun getCategories(): Resource<List<Category>>
    suspend fun getCategoryById(id: String): Resource<Category>
}