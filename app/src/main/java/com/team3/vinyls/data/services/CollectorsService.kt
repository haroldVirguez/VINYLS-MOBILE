package com.team3.vinyls.data.services

import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.data.models.CollectorDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CollectorsService {
    @GET(ApiConstants.COLLECTORS_ENDPOINT)
    suspend fun getCollectors(): List<CollectorDto>

    @GET("collectors/{id}")
    suspend fun getCollectorDetail(@Path("id") id: Int): CollectorDto
}

