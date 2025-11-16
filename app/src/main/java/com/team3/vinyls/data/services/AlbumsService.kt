package com.team3.vinyls.data.services

import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.data.models.AlbumCreateDto
import com.team3.vinyls.data.models.AlbumDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlbumsService {
    @GET(ApiConstants.ALBUMS_ENDPOINT)
    suspend fun getAlbums(): List<AlbumDto>

    @GET("${ApiConstants.ALBUMS_ENDPOINT}/{id}")
    suspend fun getAlbumDetail(@Path("id") albumId: Int): AlbumDto

    @POST(ApiConstants.ALBUMS_ENDPOINT)
    suspend fun createAlbum(@Body albumBody: AlbumCreateDto): AlbumDto
}