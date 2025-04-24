package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.SearchResult
import com.cirin0.orderflowmobile.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: String): Flow<Resource<SearchResult>>
}
