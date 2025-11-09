package com.team3.vinyls.data

import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.data.models.MusicianDto
import retrofit2.http.GET

interface MusiciansService {
    @GET(ApiConstants.MUSICIANS_ENDPOINT)
    suspend fun getMusicians(): List<MusicianDto>
}

