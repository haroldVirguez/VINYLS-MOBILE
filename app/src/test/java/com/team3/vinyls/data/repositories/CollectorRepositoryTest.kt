package com.team3.vinyls.data.repositories

import com.team3.vinyls.data.models.CollectorDto
import com.team3.vinyls.data.services.CollectorsService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CollectorRepositoryTest {

    private val service: CollectorsService = mock()
    private val repository = CollectorRepository(service)

    @Test
    fun `fetchCollectors returns list from service`() = runTest {
        val collectors = listOf(
            CollectorDto(id = 1, name = "Juan" , telephone = "300", email = "j@e.com", comments = emptyList(), favoritePerformers = emptyList(), collectorAlbums = emptyList()),
            CollectorDto(id = 2, name = "Ana", telephone = "301", email = "a@e.com", comments = emptyList(), favoritePerformers = emptyList(), collectorAlbums = emptyList())
        )

        whenever(service.getCollectors()).thenReturn(collectors)

        val result = repository.fetchCollectors()

        Assert.assertEquals(collectors, result)
    }

    @Test
    fun `fetchCollectors returns empty list when service returns empty`() = runTest {
        val empty = emptyList<CollectorDto>()
        whenever(service.getCollectors()).thenReturn(empty)

        val result = repository.fetchCollectors()

        Assert.assertEquals(0, result.size)
    }

    @Test
    fun `fetchCollectors propagates exception from service`() = runTest {
        whenever(service.getCollectors()).thenAnswer { throw RuntimeException("service failure") }

        try {
            repository.fetchCollectors()
            Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            // expected
        }
    }

    @Test
    fun `fetchCollectorDetail returns collector from service`() = runTest {
        val collector = CollectorDto(
            id = 10,
            name = "Collector Ten",
            telephone = "312",
            email = "c10@example.com",
            comments = emptyList(),
            favoritePerformers = emptyList(),
            collectorAlbums = emptyList()
        )

        whenever(service.getCollectorDetail(10)).thenReturn(collector)

        val result = repository.fetchCollectorDetail(10)

        Assert.assertNotNull(result)
        Assert.assertEquals(10, result.id)
        Assert.assertEquals("Collector Ten", result.name)
    }
}

