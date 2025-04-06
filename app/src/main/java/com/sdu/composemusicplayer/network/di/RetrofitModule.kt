package com.sdu.composemusicplayer.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sdu.composemusicplayer.network.service.LyricsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit


annotation class LyricsRetrofitService

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    val contentType = "application/json".toMediaType()
    val json = Json { ignoreUnknownKeys = true } // Optionally set your serialization preferences here

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    fun provideLyricsService(
        @LyricsRetrofitService lyricsRetrofitService: Retrofit
    ) = lyricsRetrofitService.create<LyricsService>()


    @LyricsRetrofitService
    @Provides
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl(LyricsService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

}