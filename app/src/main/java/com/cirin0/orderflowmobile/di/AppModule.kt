package com.cirin0.orderflowmobile.di

import com.cirin0.orderflowmobile.data.local.interceptor.AuthInterceptor
import com.cirin0.orderflowmobile.data.remote.AuthApi
import com.cirin0.orderflowmobile.data.remote.CategoryApi
import com.cirin0.orderflowmobile.data.remote.ProductApi
import com.cirin0.orderflowmobile.data.remote.SearchApi
import com.cirin0.orderflowmobile.data.repository.AuthRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.CategoryRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.ProductRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.SearchRepositoryImpl
import com.cirin0.orderflowmobile.domain.repository.AuthRepository
import com.cirin0.orderflowmobile.domain.repository.CategoryRepository
import com.cirin0.orderflowmobile.domain.repository.ProductRepository
import com.cirin0.orderflowmobile.domain.repository.SearchRepository
import com.cirin0.orderflowmobile.domain.usecase.GetCategoriesUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetCategoryByIdUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetProductByCategoryNameUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetProductByIdUseCase
import com.cirin0.orderflowmobile.domain.usecase.GetProductsUseCase
import com.cirin0.orderflowmobile.domain.usecase.LoginUseCase
import com.cirin0.orderflowmobile.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val baseUrl = "http://192.168.0.106:5000/"
        val secondUrl = "https://order-flow-6eb68b9f406e.herokuapp.com/"

        val finalUrl = try {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            val request = Request.Builder()
                .url(baseUrl)
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) baseUrl else secondUrl
            }
        } catch (e: Exception) {
            secondUrl
        }

        return Retrofit.Builder()
            .baseUrl(finalUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository =
        authRepositoryImpl

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductRepository(api: ProductApi): ProductRepository {
        return ProductRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGetProductsUseCase(repository: ProductRepository): GetProductsUseCase {
        return GetProductsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetProductByIdUseCase(repository: ProductRepository): GetProductByIdUseCase {
        return GetProductByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi {
        return retrofit.create(CategoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(api: CategoryApi): CategoryRepository {
        return CategoryRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGetCategoriesUseCase(repository: CategoryRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCategoryByIdUseCase(repository: CategoryRepository): GetCategoryByIdUseCase {
        return GetCategoryByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetProductByCategoryNameUseCase(repository: ProductRepository): GetProductByCategoryNameUseCase {
        return GetProductByCategoryNameUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideServiceApi(retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(api: SearchApi): SearchRepository {
        return SearchRepositoryImpl(api)
    }
}
