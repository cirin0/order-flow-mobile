package com.cirin0.orderflowmobile.data.remote

import com.cirin0.orderflowmobile.domain.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("api/search")
    suspend fun searchItems(
        @Query("query") query: String,
    ): SearchResult
}
