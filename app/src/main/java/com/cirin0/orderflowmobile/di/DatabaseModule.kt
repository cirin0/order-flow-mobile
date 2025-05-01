package com.cirin0.orderflowmobile.di

import android.content.Context
import androidx.room.Room
import com.cirin0.orderflowmobile.data.local.dao.AppDatabase
import com.cirin0.orderflowmobile.data.local.dao.FavoriteDao
import com.cirin0.orderflowmobile.data.local.interceptor.AuthInterceptor
import com.cirin0.orderflowmobile.data.repository.FavoriteRepositoryImpl
import com.cirin0.orderflowmobile.domain.repository.FavoriteRepository
import com.cirin0.orderflowmobile.util.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "orderflow_database"
        ).build()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(favoriteDao: FavoriteDao): FavoriteRepository {
        return FavoriteRepositoryImpl(favoriteDao)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }
}
