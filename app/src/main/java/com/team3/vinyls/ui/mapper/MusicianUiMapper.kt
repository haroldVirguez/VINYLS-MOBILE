package com.team3.vinyls.ui.mapper

import com.team3.vinyls.data.models.MusicianDto
import com.team3.vinyls.ui.models.MusicianUiModel

fun MusicianDto.toUi(): MusicianUiModel {
    // Prefer the musician's own description as subtitle; if missing, fall back to first album's genre
    val subtitle = this.description?.takeIf { it.isNotBlank() }
        ?: this.albums?.firstOrNull()?.genre
        ?: "Género desconocido"

    // Prefer musician image; if missing, fall back to first album cover
    val imageUrl = this.image?.takeIf { it.isNotBlank() }
        ?: this.albums?.firstOrNull()?.cover

    return MusicianUiModel(
        id = id,
        name = name,
        subtitle = subtitle,
        image = imageUrl
    )
}
