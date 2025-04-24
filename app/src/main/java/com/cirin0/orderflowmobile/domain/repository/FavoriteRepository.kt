package com.cirin0.orderflowmobile.domain.repository

import com.cirin0.orderflowmobile.domain.model.FavoriteProduct
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<FavoriteProduct>>
    suspend fun addFavorite(product: Product)
    suspend fun addFavoriteFromDetails(productDetails: ProductDetails)
    suspend fun removeFavorite(productId: Int)
    suspend fun isFavorite(productId: Int): Flow<Boolean>
}
