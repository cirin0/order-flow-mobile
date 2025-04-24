package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(productId: Int) = repository.removeFavorite(productId)
}
