package com.team3.vinyls.data.services

import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.data.models.TrackDto
import retrofit2.http.*

interface TrackService {

    @GET("${ApiConstants.ALBUMS_ENDPOINT}/{albumId}/tracks")
    suspend fun getTracksByAlbum(
        @Path("albumId") albumId: Int
    ): List<TrackDto>

    @POST("${ApiConstants.ALBUMS_ENDPOINT}/{albumId}/tracks")
    suspend fun addTrackToAlbum(
        @Path("albumId") albumId: Int,
        @Body track: TrackDto
    ): TrackDto
}