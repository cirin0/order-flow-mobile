package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.remote.SearchApi
import com.cirin0.orderflowmobile.domain.model.SearchResult
import com.cirin0.orderflowmobile.domain.repository.SearchRepository
import com.cirin0.orderflowmobile.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val api: SearchApi,
) : SearchRepository {
    override fun search(query: String): Flow<Resource<SearchResult>> = flow {
        try {
            emit(Resource.Loading())
            val searchResult = api.searchItems(query)
            emit(Resource.Success(searchResult))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}
