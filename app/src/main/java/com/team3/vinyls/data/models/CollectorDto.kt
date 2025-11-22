package com.team3.vinyls.data.models

import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.models.PerformerDto
import com.team3.vinyls.data.models.CommentDto

data class CollectorDto(
    val id: Int,
    val name: String,
    val telephone: String? = null,
    val email: String? = null,
    val comments: List<CommentDto>? = null,
    val favoritePerformers: List<PerformerDto>? = null,
    val collectorAlbums: List<AlbumDto>? = null
)

