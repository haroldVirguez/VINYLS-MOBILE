package com.team3.vinyls.albums.data

import com.team3.vinyls.core.network.AlbumDto
import com.team3.vinyls.core.network.AlbumsService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumRepositoryTest {

    private class FakeAlbumsService(private val albums: List<AlbumDto>) : AlbumsService {
        override suspend fun getAlbums(): List<AlbumDto> = albums
    }

    @Test
    fun `fetchAlbums maps AlbumDto to AlbumUiModel`() = runTest {
        val dto = AlbumDto(
            id = 1, 
            name = "Abbey Road", 
            cover = "cover.jpg",
            releaseDate = "1969-09-26",
            description = "The Beatles' final album",
            genre = "Rock",
            recordLabel = "Apple Records"
        )
        val service = FakeAlbumsService(listOf(dto))
        val repo = AlbumRepository(service)

        val result = repo.fetchAlbums()

        assertEquals(1, result.size)
        val ui = result[0]
        assertEquals(1, ui.id)
        assertEquals("Abbey Road", ui.title)
        assertEquals("Artista desconocido • 1969", ui.subtitle)
    }
}

