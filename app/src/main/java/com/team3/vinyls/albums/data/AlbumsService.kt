package com.team3.vinyls.albums.data

import com.team3.vinyls.core.network.ApiConstants
import retrofit2.http.GET

data class AlbumDto(
    val id: Int,
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
    val tracks: List<TrackDto>? = null,
    val performers: List<PerformerDto>? = null,
    val comments: List<CommentDto>? = null
)

data class TrackDto(
    val id: Int,
    val name: String,
    val duration: String
)

data class PerformerDto(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String? = null
)

data class CommentDto(
    val id: Int,
    val description: String,
    val rating: Int
)

interface AlbumsService {
    @GET(ApiConstants.ALBUMS_ENDPOINT)
    suspend fun getAlbums(): List<AlbumDto>

    @GET("${ApiConstants.ALBUMS_ENDPOINT}/{id}")
    suspend fun getAlbumDetail(@retrofit2.http.Path("id") albumId: Int): AlbumDto
}
