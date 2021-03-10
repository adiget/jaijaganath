package com.ags.annada.jagannath.di

import com.ags.annada.jagannath.datasource.network.api.ApiService
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.API_KEY
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.BASE_YOUTUBE_URL
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.KEY_NAME
import com.ags.annada.jagannath.utils.FlowCallAdapterFactory
import com.ags.annada.jagannathauk.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    internal fun providesRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_YOUTUBE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory())
            .client(providesOkHttpClient())
            .build()
    }

    @Singleton
    @Provides
    internal fun providesOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.NONE
            }

            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        okHttpClientBuilder.addInterceptor { chain ->
            var request: Request = chain.request()
            val url = request.url.newBuilder().addQueryParameter(KEY_NAME, API_KEY).build()
            request = request.newBuilder().url(url).build()
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