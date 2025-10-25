package com.team3.vinyls.albums.data

import com.team3.vinyls.albums.ui.AlbumUiModel

open class AlbumRepository(private val service: AlbumsService) {
    open suspend fun fetchAlbums(): List<AlbumUiModel> {
        val albums = service.getAlbums()
        return albums.map { dto ->
            AlbumUiModel(
                id = dto.id,
                title = dto.name,
                subtitle = formatSubtitle(dto),
                cover = dto.cover,
                description = dto.description,
                genre = dto.genre,
                recordLabel = dto.recordLabel,
                releaseDate = dto.releaseDate
            )
        }
    }

    suspend fun getAlbumDetail(albumId: Int): AlbumDto {
        return try {
            service.getAlbumDetail(albumId)
        } catch (e: Exception) {
            throw Exception("Error al obtener detalle del álbum: ${e.message}")
        }
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
            val date = java.time.LocalDate.parse(dateString.substring(0, 10))
            date.year.toString()
        } catch (e: Exception) {
            "N/A"
        }
    }
}
