package com.team3.vinyls.ui.models

data class AlbumUiModel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val cover: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
    val releaseDate: String
)