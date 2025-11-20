package com.team3.vinyls.data.repositories

import com.team3.vinyls.data.models.AlbumCreateDto
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.services.AlbumsService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class AlbumRepositoryEdgeCasesTest {

    private class FakeAlbumsService(private val behavior: Behavior) : AlbumsService {
        enum class Behavior { EMPTY, THROW }
        override suspend fun getAlbums(): List<AlbumDto> {
            return when (behavior) {
                Behavior.EMPTY -> emptyList()
                Behavior.THROW -> throw IllegalArgumentException("fake error")
            }
        }

        override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
            // Como este test solo verifica getAlbums, puedes dejarlo como dummy
            throw NotImplementedError("FakeAlbumsService.getAlbumDetail() no implementado en este test")
        }

        override suspend fun createAlbum(albumBody: AlbumCreateDto): AlbumDto {
            TODO("Not yet implemented")
        }
    }

    private fun repoWithBehavior(behavior: FakeAlbumsService.Behavior): AlbumRepository {
        val service = FakeAlbumsService(behavior)
        return AlbumRepository(service)
    }

    @Test
    fun emptyList_returnsEmptyUiList() {
        val result =
            runBlocking { repoWithBehavior(FakeAlbumsService.Behavior.EMPTY).fetchAlbums() }
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun serviceThrows_propagatesException() {
        try {
            runBlocking { repoWithBehavior(FakeAlbumsService.Behavior.THROW).fetchAlbums() }
            // if no exception, fail
            Assert.assertTrue("Expected exception", false)
        } catch (t: Throwable) {
            Assert.assertTrue(t is IllegalArgumentException)
        }
    }
}