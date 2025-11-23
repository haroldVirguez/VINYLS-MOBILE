package com.team3.vinyls.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.team3.vinyls.data.models.CollectorDto
import com.team3.vinyls.data.models.CommentDto
import com.team3.vinyls.data.models.PerformerDto
import com.team3.vinyls.data.repositories.CollectorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class CollectorsDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CollectorRepository

    @Mock
    private lateinit var observer: Observer<CollectorDto?>

    private lateinit var viewModel: CollectorsDetailViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)
        viewModel = CollectorsDetailViewModel(repository)
        viewModel.collector.observeForever(observer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCollectorDetail should post collector data`() = runTest {
        val mockCollector = CollectorDto(
            id = 1,
            name = "Juan Pérez",
            telephone = "3001234567",
            email = "juan@example.com",
            comments = listOf(
                CommentDto(id = 1, description = "Excelente coleccionista", rating = 5)
            ),
            favoritePerformers = listOf(
                PerformerDto(
                    id = 1,
                    name = "Rubén Blades",
                    image = "image.jpg",
                    description = "Artista de salsa",
                    birthDate = "1948-07-16"
                )
            ),
            collectorAlbums = emptyList()
        )

        `when`(repository.fetchCollectorDetail(1)).thenReturn(mockCollector)

        viewModel.loadCollectorDetail(1)
        dispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(mockCollector)
        assertEquals(mockCollector, viewModel.collector.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadCollectorDetail should set loading to true then false`() = runTest {
        val mockCollector = CollectorDto(
            id = 1,
            name = "Test Collector",
            telephone = "3001234567",
            email = "test@example.com",
            comments = emptyList(),
            favoritePerformers = emptyList(),
            collectorAlbums = emptyList()
        )

        `when`(repository.fetchCollectorDetail(1)).thenReturn(mockCollector)

        assertEquals(false, viewModel.loading.value)

        viewModel.loadCollectorDetail(1)
        assertEquals(true, viewModel.loading.value)

        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.loading.value)
        assertEquals(mockCollector, viewModel.collector.value)
    }

    @Test
    fun `loadCollectorDetail should handle error and set error message`() = runTest {
        val errorMessage = "Network error"
        `when`(repository.fetchCollectorDetail(1)).thenThrow(RuntimeException(errorMessage))

        viewModel.loadCollectorDetail(1)
        dispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.collector.value)
        assertEquals(errorMessage, viewModel.error.value)
        assertEquals(false, viewModel.loading.value)
    }

    @Test
    fun `loadCollectorDetail should clear previous error on new load`() = runTest {
        val errorMessage = "First error"
        `when`(repository.fetchCollectorDetail(1)).thenThrow(RuntimeException(errorMessage))

        viewModel.loadCollectorDetail(1)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(errorMessage, viewModel.error.value)

        val mockCollector = CollectorDto(
            id = 1,
            name = "Test Collector",
            telephone = "3001234567",
            email = "test@example.com",
            comments = emptyList(),
            favoritePerformers = emptyList(),
            collectorAlbums = emptyList()
        )

        `when`(repository.fetchCollectorDetail(2)).thenReturn(mockCollector)

        viewModel.loadCollectorDetail(2)
        dispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.error.value)
        assertEquals(mockCollector, viewModel.collector.value)
    }

    @Test
    fun `loadCollectorDetail should handle collector with null optional fields`() = runTest {
        val mockCollector = CollectorDto(
            id = 2,
            name = "Collector Sin Datos",
            telephone = null,
            email = null,
            comments = null,
            favoritePerformers = null,
            collectorAlbums = null
        )

        `when`(repository.fetchCollectorDetail(2)).thenReturn(mockCollector)

        viewModel.loadCollectorDetail(2)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockCollector, viewModel.collector.value)
        assertNotNull(viewModel.collector.value)
        assertEquals("Collector Sin Datos", viewModel.collector.value?.name)
    }

    @Test
    fun `loadCollectorDetail should handle collector with multiple comments`() = runTest {
        val mockCollector = CollectorDto(
            id = 3,
            name = "Collector Popular",
            telephone = "3001234567",
            email = "popular@example.com",
            comments = listOf(
                CommentDto(id = 1, description = "Muy bueno", rating = 4),
                CommentDto(id = 2, description = "Excelente", rating = 5),
                CommentDto(id = 3, description = "Regular", rating = 3)
            ),
            favoritePerformers = emptyList(),
            collectorAlbums = emptyList()
        )

        `when`(repository.fetchCollectorDetail(3)).thenReturn(mockCollector)

        viewModel.loadCollectorDetail(3)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockCollector, viewModel.collector.value)
        assertEquals(3, viewModel.collector.value?.comments?.size)
    }

    @Test
    fun `loadCollectorDetail should handle collector with multiple favorite performers`() = runTest {
        val mockCollector = CollectorDto(
            id = 4,
            name = "Collector Fanático",
            telephone = "3001234567",
            email = "fanatico@example.com",
            comments = emptyList(),
            favoritePerformers = listOf(
                PerformerDto(
                    id = 1,
                    name = "Artista 1",
                    image = "image1.jpg",
                    description = "Descripción 1",
                    birthDate = "1980-01-01"
                ),
                PerformerDto(
                    id = 2,
                    name = "Artista 2",
                    image = "image2.jpg",
                    description = "Descripción 2",
                    birthDate = "1990-01-01"
                )
            ),
            collectorAlbums = emptyList()
        )

        `when`(repository.fetchCollectorDetail(4)).thenReturn(mockCollector)

        viewModel.loadCollectorDetail(4)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockCollector, viewModel.collector.value)
        assertEquals(2, viewModel.collector.value?.favoritePerformers?.size)
    }
}

