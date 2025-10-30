package com.team3.vinyls.data.models

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