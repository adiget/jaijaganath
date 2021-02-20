package com.ags.annada.jagannath.di

import com.ags.annada.jagannath.datasource.network.api.ApiService
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.API_KEY
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.BASE_YOUTUBE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    internal fun providesRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_YOUTUBE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(providesOkHttpClient())
            .build()
    }

    @Singleton
    @Provides
    internal fun providesOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        okHttpClientBuilder.addInterceptor(loggingInterceptor)

        okHttpClientBuilder.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder().addHeader("key", API_KEY).build()
            chain.proceed(request)
        }

        return okHttpClientBuilder.build()
    }

    @Singleton
    @Provides
    internal fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}