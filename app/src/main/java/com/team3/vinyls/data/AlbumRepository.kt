package com.team3.vinyls.data

import com.team3.vinyls.data.models.AlbumDto
import java.time.LocalDate

open class AlbumRepository(private val service: AlbumsService) {
    open suspend fun fetchAlbums(): List<AlbumDto> {
        val albums = service.getAlbums()
        return albums.map { dto ->dto
        }
    }

    suspend fun getAlbumDetail(albumId: Int): AlbumDto {
        return try {
            service.getAlbumDetail(albumId)
        } catch (e: Exception) {
            throw Exception("Error al obtener detalle del álbum: ${e.message}")
        }
    }
}
