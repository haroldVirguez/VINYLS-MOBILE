package com.team3.vinyls.data.services

import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.data.models.MusicianDto
import retrofit2.http.GET
import retrofit2.http.Path

interface MusiciansService {
    @GET(ApiConstants.MUSICIANS_ENDPOINT)
    suspend fun getMusicians(): List<MusicianDto>

    @GET("musicians/{id}")
    suspend fun getMusicianDetail(@Path("id") id: Int
    ): MusicianDto
}