package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(productId: Int): Flow<Boolean> = repository.isFavorite(productId)
}
