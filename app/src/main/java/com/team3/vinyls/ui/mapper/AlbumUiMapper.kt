package com.team3.vinyls.ui.mapper

import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.ui.models.AlbumUiModel
import java.time.LocalDate

fun AlbumDto.toUi(): AlbumUiModel {
    return AlbumUiModel(
        id = id,
        title = name,
        subtitle = formatSubtitle(this),
        cover = cover,
        description = description,
        genre = genre,
        recordLabel = recordLabel,
        releaseDate = releaseDate
    )
}

private fun formatSubtitle(dto: AlbumDto): String {
    val performers = dto.performers?.map { it.name } ?: emptyList()
    val artistNames = if (performers.isNotEmpty()) {
        performers.joinToString(" - ")
    } else {
        "Artista desconocido"
    }

    val year = extractYearFromDate(dto.releaseDate)
    return "$artistNames • $year"
}

private fun extractYearFromDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString.substring(0, 10))
        date.year.toString()
    } catch (e: Exception) {
        "N/A"
    }
}