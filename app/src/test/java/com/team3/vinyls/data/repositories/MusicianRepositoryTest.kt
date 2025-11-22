package com.team3.vinyls.data.repositories

import com.team3.vinyls.data.models.MusicianDto
import com.team3.vinyls.data.services.MusiciansService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MusicianRepositoryTest {

    private val service: MusiciansService = mock()
    private val repository = MusicianRepository(service)

    @Test
    fun `fetchMusicians returns list from service`() = runTest {
        val musicians = listOf(
            MusicianDto(id = 1, name = "John Doe"),
            MusicianDto(id = 2, name = "Jane Smith")
        )

        // Stub the suspend function
        whenever(service.getMusicians()).thenReturn(musicians)

        val result = repository.fetchMusicians()

        Assert.assertEquals(musicians, result)
    }

    @Test
    fun `fetchMusicians returns empty list when service returns empty`() = runTest {
        val empty = emptyList<MusicianDto>()
        whenever(service.getMusicians()).thenReturn(empty)

        val result = repository.fetchMusicians()

        Assert.assertEquals(0, result.size)
    }

    @Test
    fun `fetchMusicians propages exception from service`() = runTest {
        whenever(service.getMusicians()).thenAnswer { throw RuntimeException("service failure") }

        try {
            repository.fetchMusicians()
            Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            // expected
        }
    }

    @Test
    fun `fetchMusicianDetail returns musician from service`() = runTest {
        val musician = MusicianDto(
            id = 10,
            name = "Rubén Blades",
            image = "image.jpg",
            description = "Cantante",
            birthDate = "1948-07-16",
            albums = emptyList(),
            performerPrizes = emptyList()
        )

        whenever(service.getMusicianDetail(10)).thenReturn(musician)

        val result = repository.fetchMusicianDetail(10)

        Assert.assertNotNull(result)
        Assert.assertEquals(10, result?.id)
        Assert.assertEquals("Rubén Blades", result?.name)
    }
}