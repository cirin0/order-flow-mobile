package com.cirin0.orderflowmobile.data.repository

import com.cirin0.orderflowmobile.data.local.dao.FavoriteDao
import com.cirin0.orderflowmobile.domain.model.FavoriteProduct
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {
    override fun getAllFavorites(): Flow<List<FavoriteProduct>> =
        favoriteDao.getAllFavorites()

    override suspend fun addFavorite(product: Product) {
        val favoriteProduct = FavoriteProduct.fromProduct(product)
        favoriteDao.insertFavorite(favoriteProduct)
    }

    override suspend fun addFavoriteFromDetails(productDetails: ProductDetails) {
        val favoriteProduct = FavoriteProduct.fromProductDetails(productDetails)
        favoriteDao.insertFavorite(favoriteProduct)
    }

    override suspend fun removeFavorite(productId: Int) {
        favoriteDao.deleteFavoriteById(productId)
    }

    override suspend fun isFavorite(productId: Int): Flow<Boolean> =
        favoriteDao.isFavorite(productId)

}
