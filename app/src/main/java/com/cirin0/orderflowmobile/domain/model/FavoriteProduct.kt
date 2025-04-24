package com.cirin0.orderflowmobile.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteProduct(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val addedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromProduct(product: Product): FavoriteProduct {
            return FavoriteProduct(
                id = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price,
            )
        }

        fun fromProductDetails(productDetails: ProductDetails): FavoriteProduct {
            return FavoriteProduct(
                id = productDetails.id,
                name = productDetails.name,
                imageUrl = productDetails.imageUrl,
                price = productDetails.price,
            )
        }
    }
}
