package com.team3.vinyls.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    fun provideTrackService(baseUrl: String): com.team3.vinyls.data.services.TrackService {
        return retrofit(baseUrl).create(com.team3.vinyls.data.services.TrackService::class.java)
    }

    fun provideTrackRepository(baseUrl: String): com.team3.vinyls.data.repositories.TrackRepository {
        val trackService = provideTrackService(baseUrl)
        return com.team3.vinyls.data.repositories.TrackRepository(trackService)
    }
}
