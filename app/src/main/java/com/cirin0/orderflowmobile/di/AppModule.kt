package com.cirin0.orderflowmobile.di

import com.cirin0.orderflowmobile.data.local.interceptor.AuthInterceptor
import com.cirin0.orderflowmobile.data.remote.AuthApi
import com.cirin0.orderflowmobile.data.remote.CategoryApi
import com.cirin0.orderflowmobile.data.remote.ProductApi
import com.cirin0.orderflowmobile.data.remote.ReviewApi
import com.cirin0.orderflowmobile.data.remote.SearchApi
import com.cirin0.orderflowmobile.data.repository.AuthRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.CategoryRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.ProductRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.ReviewRepositoryImpl
import com.cirin0.orderflowmobile.data.repository.SearchRepositoryImpl
import com.cirin0.orderflowmobile.domain.repository.AuthRepository
import com.cirin0.orderflowmobile.domain.repository.CategoryRepository
import com.cirin0.orderflowmobile.domain.repository.ProductRepository
import com.cirin0.orderflowmobile.domain.repository.ReviewRepository
import com.cirin0.orderflowmobile.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
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
    fun provideConverterFactory(): Converter.Factory {
        return object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type,
                annotations: Array<out Annotation>,
                retrofit: Retrofit
            ): Converter<ResponseBody, *>? {
                if (type == String::class.java) {
                    return Converter<ResponseBody, String> { value -> value.string() }
                }
                return null
            }
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        textConverter: Converter.Factory,
    ): Retrofit {
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
            .addConverterFactory(textConverter)
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
    fun provideSearchApi(retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(api: SearchApi): SearchRepository {
        return SearchRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideReviewApi(retrofit: Retrofit): ReviewApi {
        return retrofit.create(ReviewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(api: ReviewApi): ReviewRepository {
        return ReviewRepositoryImpl(api)
    }
}
