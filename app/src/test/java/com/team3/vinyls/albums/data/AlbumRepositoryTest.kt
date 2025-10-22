package com.team3.vinyls.albums.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumRepositoryTest {

    private class FakeAlbumsService(private val albums: List<AlbumDto>) : AlbumsService {
        override suspend fun getAlbums(): List<AlbumDto> = albums
    }

    @Test
    fun `fetchAlbums maps AlbumDto to AlbumUiModel`() = runTest {
        val dto = AlbumDto(id = "1", name = "Abbey Road", artist = "The Beatles", year = 1969)
        val service = FakeAlbumsService(listOf(dto))
        val repo = AlbumRepository(service)

        val result = repo.fetchAlbums()

        assertEquals(1, result.size)
        val ui = result[0]
        assertEquals("1", ui.id)
        assertEquals("Abbey Road", ui.title)
        assertEquals("The Beatles • 1969", ui.subtitle)
    }
}

