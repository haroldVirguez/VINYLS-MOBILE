package com.team3.vinyls.albums.data

import com.team3.vinyls.core.network.ApiConstants
import retrofit2.http.GET

data class AlbumDto(
    val id: String,
    val name: String,
    val artist: String,
    val year: Int
)

interface AlbumsService {
    @GET(ApiConstants.ALBUMS_ENDPOINT)
    suspend fun getAlbums(): List<AlbumDto>
}


