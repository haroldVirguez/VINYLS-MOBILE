package com.team3.vinyls.albums

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import com.team3.vinyls.core.network.AlbumDto
import com.team3.vinyls.core.network.AlbumsService
import com.team3.vinyls.albums.data.AlbumRepository

class AlbumRepositoryEdgeCasesTest {

    private class FakeAlbumsService(private val behavior: Behavior) : AlbumsService {
        enum class Behavior { EMPTY, THROW }
        override suspend fun getAlbums(): List<AlbumDto> {
            return when (behavior) {
                Behavior.EMPTY -> emptyList()
                Behavior.THROW -> throw IllegalArgumentException("fake error")
            }
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
