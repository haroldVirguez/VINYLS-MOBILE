package com.team3.vinyls.data.models

data class AlbumCreateDto(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
)
