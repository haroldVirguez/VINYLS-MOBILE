package com.team3.vinyls.data

import com.team3.vinyls.data.models.MusicianDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals
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

        assertEquals(musicians, result)
    }

    @Test
    fun `fetchMusicians returns empty list when service returns empty`() = runTest {
        val empty = emptyList<MusicianDto>()
        whenever(service.getMusicians()).thenReturn(empty)

        val result = repository.fetchMusicians()

        assertEquals(0, result.size)
    }

    @Test
    fun `fetchMusicians propages exception from service`() = runTest {
        whenever(service.getMusicians()).thenAnswer { throw RuntimeException("service failure") }

        try {
            repository.fetchMusicians()
            org.junit.Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            // expected
        }
    }
}
