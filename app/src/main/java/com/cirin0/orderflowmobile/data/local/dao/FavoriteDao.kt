package com.cirin0.orderflowmobile.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cirin0.orderflowmobile.domain.model.FavoriteProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteProduct)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteProduct)

    @Query("DELETE FROM favorites WHERE id = :productId")
    suspend fun deleteFavoriteById(productId: Int)

    @Query("SELECT * FROM favorites WHERE id = :productId")
    suspend fun getFavoriteById(productId: Int): FavoriteProduct?

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :productId LIMIT 1)")
    fun isFavorite(productId: Int): Flow<Boolean>
}
