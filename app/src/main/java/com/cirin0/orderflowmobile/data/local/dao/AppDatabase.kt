package com.cirin0.orderflowmobile.data.local.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cirin0.orderflowmobile.domain.model.FavoriteProduct

@Database(
    entities = [FavoriteProduct::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
