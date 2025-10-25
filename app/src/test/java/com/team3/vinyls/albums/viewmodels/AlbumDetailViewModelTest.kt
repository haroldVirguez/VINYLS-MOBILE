package com.team3.vinyls.albums.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.albums.data.AlbumDto
import com.team3.vinyls.albums.data.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumDetailViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emite album cuando repository devuelve detalle`() = runTest {
        val fakeAlbum = AlbumDto(
            id = 1,
            name = "Buscando América",
            cover = "cover.jpg",
            releaseDate = "1984-08-01",
            description = "Primer álbum",
            genre = "Salsa",
            recordLabel = "Elektra"
        )

        val fakeRepo = object : AlbumRepository(service = object : com.team3.vinyls.albums.data.AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int) = fakeAlbum
        }) {}

        val viewModel = AlbumDetailViewModel(fakeRepo)
        viewModel.loadAlbumDetail(1)
        advanceUntilIdle()

        assertEquals("Buscando América", viewModel.album.value?.name)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `emite error cuando falla el repository`() = runTest {
        val fakeRepo = object : AlbumRepository(service = object : com.team3.vinyls.albums.data.AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
                throw RuntimeException("Error de red")
            }
        }) {}

        val viewModel = AlbumDetailViewModel(fakeRepo)
        viewModel.loadAlbumDetail(1)
        advanceUntilIdle()

        assertNull(viewModel.album.value)
        assertEquals(
            "Error al obtener detalle del álbum: Error de red",
            viewModel.error.value
        )
    }

    @Test
    fun `loading cambia de true a false durante la carga`() = runTest {
        val fakeAlbum = AlbumDto(
            id = 1,
            name = "Buscando América",
            cover = "cover.jpg",
            releaseDate = "1984-08-01",
            description = "Primer álbum",
            genre = "Salsa",
            recordLabel = "Elektra"
        )

        val fakeRepo = object : AlbumRepository(service = object : com.team3.vinyls.albums.data.AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
                // simulamos un pequeño retraso
                kotlinx.coroutines.delay(50)
                return fakeAlbum
            }
        }) {}

        val viewModel = AlbumDetailViewModel(fakeRepo)

        assertEquals(false, viewModel.loading.value)

        viewModel.loadAlbumDetail(1)
        assertEquals(true, viewModel.loading.value) // empieza a cargar

        advanceUntilIdle() // dejamos que termine

        assertEquals(false, viewModel.loading.value) // termina la carga
    }
}