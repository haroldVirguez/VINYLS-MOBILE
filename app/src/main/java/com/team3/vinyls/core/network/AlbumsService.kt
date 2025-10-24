package com.team3.vinyls.core.network

import retrofit2.http.GET
import retrofit2.http.Path

data class AlbumDto(
    val id: Int,
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String
)

interface AlbumsService {
    @GET("albums")
    suspend fun getAlbums(): List<AlbumDto>

    @GET("albums/{id}")
    suspend fun getAlbumById(@Path("id") albumId: String): AlbumDto
}
