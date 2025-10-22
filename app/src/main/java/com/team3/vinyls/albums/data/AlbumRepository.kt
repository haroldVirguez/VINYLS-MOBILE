package com.team3.vinyls.albums.data

import com.team3.vinyls.albums.ui.AlbumUiModel

open class AlbumRepository(private val service: AlbumsService) {
    open suspend fun fetchAlbums(): List<AlbumUiModel> {
        return service.getAlbums().map { dto ->
            AlbumUiModel(
                id = dto.id,
                title = dto.name,
                subtitle = "${dto.artist} • ${dto.year}"
            )
        }
    }
}
