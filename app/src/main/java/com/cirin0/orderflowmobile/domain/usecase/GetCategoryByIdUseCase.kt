package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.Category
import com.cirin0.orderflowmobile.domain.repository.CategoryRepository
import com.cirin0.orderflowmobile.util.Resource
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: String): Resource<Category> {
        return repository.getCategoryById(id)
    }
}