package com.team3.vinyls

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.services.AlbumsService
import com.team3.vinyls.data.repositories.AlbumRepository

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
    }

    private fun repoWithBehavior(behavior: FakeAlbumsService.Behavior): AlbumRepository {
        val service = FakeAlbumsService(behavior)
        return AlbumRepository(service)
    }

    @Test
    fun emptyList_returnsEmptyUiList() {
        val result = runBlocking { repoWithBehavior(FakeAlbumsService.Behavior.EMPTY).fetchAlbums() }
        assertTrue(result.isEmpty())
    }

    @Test
    fun serviceThrows_propagatesException() {
        try {
            runBlocking { repoWithBehavior(FakeAlbumsService.Behavior.THROW).fetchAlbums() }
            // if no exception, fail
            assertTrue("Expected exception", false)
        } catch (t: Throwable) {
            assertTrue(t is IllegalArgumentException)
        }
    }
}
