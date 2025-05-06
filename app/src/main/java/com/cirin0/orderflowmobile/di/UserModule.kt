package com.cirin0.orderflowmobile.di

import com.cirin0.orderflowmobile.data.remote.PasswordResetApi
import com.cirin0.orderflowmobile.data.remote.UserApi
import com.cirin0.orderflowmobile.data.repository.PasswordResetRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.UserRepositoryImpl
import com.cirin0.orderflowmobile.domain.repository.PasswordResetRepository
import com.cirin0.orderflowmobile.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Singleton
    @Provides
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUserRepository(userApiService: UserApi): UserRepository {
        return UserRepositoryImpl(userApiService)
    }

    @Singleton
    @Provides
    fun providePasswordResetApi(retrofit: Retrofit): PasswordResetApi {
        return retrofit.create(PasswordResetApi::class.java)
    }

    @Singleton
    @Provides
    fun providePasswordResetRepository(passwordApiService: PasswordResetApi): PasswordResetRepository {
        return PasswordResetRepositoryImpl(passwordApiService)
    }

}
