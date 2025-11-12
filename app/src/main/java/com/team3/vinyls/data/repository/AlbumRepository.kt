package com.team3.vinyls.data.repository

import com.team3.vinyls.data.service.AlbumsService
import com.team3.vinyls.data.models.AlbumDto

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