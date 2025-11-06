package com.team3.vinyls.data.models

data class PerformerPrizeDto(
    val id: Int,
    val premiationDate: String?
)

data class MusicianDto(
    val id: Int,
    val name: String,
    val image: String? = null,
    val description: String? = null,
    val birthDate: String? = null,
    val albums: List<AlbumDto>? = null,
    val performerPrizes: List<PerformerPrizeDto>? = null
)

