package com.team3.vinyls.data.repositories

import com.team3.vinyls.data.models.AlbumCreateDto
import com.team3.vinyls.data.services.AlbumsService
import com.team3.vinyls.data.models.AlbumDto

open class AlbumRepository(private val service: AlbumsService) {
    open suspend fun fetchAlbums(): List<AlbumDto> {
        return service.getAlbums()
    }

    suspend fun getAlbumDetail(albumId: Int): AlbumDto {
        return try {
            service.getAlbumDetail(albumId)
        } catch (e: Exception) {
            throw Exception("Error al obtener detalle del álbum: ${e.message}")
        }
    }

    suspend fun createAlbum(album: AlbumCreateDto): AlbumDto {
        return try {
            service.createAlbum(album)
        } catch (e: Exception) {
            throw Exception("Error al crear el album: ${e.message}")
        }
    }
}