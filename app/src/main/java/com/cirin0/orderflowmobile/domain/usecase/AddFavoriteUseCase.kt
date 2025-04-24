package com.cirin0.orderflowmobile.domain.usecase

import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.repository.FavoriteRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(product: Product) = repository.addFavorite(product)
    suspend operator fun invoke(productDetails: ProductDetails) =
        repository.addFavoriteFromDetails(productDetails)
}
