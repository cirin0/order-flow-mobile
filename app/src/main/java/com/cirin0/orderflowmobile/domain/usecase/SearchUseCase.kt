package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.SearchResult
import com.cirin0.orderflowmobile.domain.repository.SearchRepository
import com.cirin0.orderflowmobile.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    operator fun invoke(query: String): Flow<Resource<SearchResult>> {
        return repository.search(query)
    }
}
