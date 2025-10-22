package com.team3.vinyls.albums.data

import retrofit2.http.GET

data class AlbumDto(
    val id: String,
    val name: String,
    val artist: String,
    val year: Int
)

interface AlbumsService {
    @GET("albums")
    suspend fun getAlbums(): List<AlbumDto>
}


