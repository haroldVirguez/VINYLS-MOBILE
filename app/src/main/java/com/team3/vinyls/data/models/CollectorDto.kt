package com.team3.vinyls.data.models

data class CollectorDto(
    val id: Int,
    val name: String,
    val telephone: String? = null,
    val email: String? = null,
    val comments: List<CommentDto>? = null,
    val favoritePerformers: List<PerformerDto>? = null,
    val collectorAlbums: List<CollectorAlbumDto>? = null
)
