package com.team3.vinyls.albums.data

import com.team3.vinyls.albums.ui.AlbumUiModel
import com.team3.vinyls.core.network.AlbumsService

open class AlbumRepository(private val service: AlbumsService) {

    open suspend fun fetchAlbums(): List<AlbumUiModel> {
        return service.getAlbums().map { dto ->
            AlbumUiModel(
                id = dto.id.toString(),
                title = dto.name,
                subtitle = "${dto.recordLabel} • ${dto.genre}"
            )
        }
    }

    open suspend fun getAlbumDetail(albumId: String): AlbumUiModel {
        val dto = service.getAlbumById(albumId)
        return AlbumUiModel(
            id = dto.id.toString(),
            title = dto.name,
            subtitle = "${dto.recordLabel} • ${dto.genre}"
        )
    }
}