package com.team3.vinyls.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.data.models.AlbumCreateDto
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.models.TrackDto
import com.team3.vinyls.data.repositories.AlbumRepository
import com.team3.vinyls.data.repositories.TrackRepository
import com.team3.vinyls.data.services.TrackService
import com.team3.vinyls.data.services.AlbumsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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

    private class FakeAlbumRepo(val album: AlbumDto?) : AlbumRepository(
        service = object : AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int) =
                album ?: throw RuntimeException("Album no encontrado")

            override suspend fun createAlbum(albumBody: AlbumCreateDto): AlbumDto {
                TODO("Not yet implemented")
            }
        }
    )

    private class FakeTrackRepo(
        val tracks: List<TrackDto> = emptyList(),
        val shouldFail: Boolean = false
    ) : TrackRepository(
        service = object : TrackService {

            override suspend fun getTracksByAlbum(albumId: Int): List<TrackDto> {
                if (shouldFail) throw RuntimeException("Falla pista")
                return tracks
            }

            override suspend fun addTrackToAlbum(albumId: Int, dto: TrackDto): TrackDto {
                if (shouldFail) throw RuntimeException("No se pudo agregar track")
                return dto.copy(id = 999)
            }
        }
    )

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

        val fakeRepo = object : AlbumRepository(service = object : AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int) = fakeAlbum
            override suspend fun createAlbum(albumBody: AlbumCreateDto): AlbumDto {
                TODO("Not yet implemented")
            }
        }) {}

        val viewModel = AlbumDetailViewModel(fakeRepo, FakeTrackRepo(), dispatcher = dispatcher)
        viewModel.loadAlbumDetail(1)
        advanceUntilIdle()

        assertEquals("Buscando América", viewModel.album.value?.name)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `emite error cuando falla el repository`() = runTest {
        val fakeRepo = object : AlbumRepository(service = object : AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
                throw RuntimeException("Error de red")
            }
            override suspend fun createAlbum(albumBody: AlbumCreateDto): AlbumDto {
                TODO("Not yet implemented")
            }
        }) {}

        val viewModel = AlbumDetailViewModel(fakeRepo, FakeTrackRepo(), dispatcher = dispatcher)
        viewModel.loadAlbumDetail(1)
        advanceUntilIdle()

        assertNull(viewModel.album.value)
        assertEquals(
            "Error al obtener detalle del álbum: Error de red",
            viewModel.error.value
        )
    }

    @Test
    fun `loading cambia correctamente en carga diferida`() = runTest {
        val fakeAlbum = AlbumDto(
            id = 1,
            name = "Buscando América",
            cover = "cover.jpg",
            releaseDate = "1984-08-01",
            description = "Primer álbum",
            genre = "Salsa",
            recordLabel = "Elektra"
        )

        val fakeRepo = object : AlbumRepository(service = object : AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
                // simulamos un pequeño retraso
                delay(50)
                return fakeAlbum
            }

            override suspend fun createAlbum(albumBody: AlbumCreateDto): AlbumDto {
                TODO("Not yet implemented")
            }
        }) {}

        val viewModel = AlbumDetailViewModel(fakeRepo, FakeTrackRepo(), dispatcher = dispatcher)

        assertEquals(false, viewModel.loading.value)

        viewModel.loadAlbumDetail(1)
        assertEquals(true, viewModel.loading.value) // empieza a cargar

        advanceUntilIdle() // dejamos que termine

        assertEquals(false, viewModel.loading.value) // termina la carga
    }

    @Test
    fun `loadAlbumDetail emite album correctamente`() = runTest {
        val fakeAlbum = AlbumDto(
            id = 1,
            name = "Buscando América",
            cover = "cover.jpg",
            releaseDate = "1984-08-01",
            description = "Primer álbum",
            genre = "Salsa",
            recordLabel = "Elektra"
        )

        val vm = AlbumDetailViewModel(
            albumRepository = FakeAlbumRepo(fakeAlbum),
            trackRepository = FakeTrackRepo(),
            dispatcher = dispatcher
        )

        vm.loadAlbumDetail(1)
        advanceUntilIdle()

        assertEquals("Buscando América", vm.album.value?.name)
        assertNull(vm.error.value)
    }

    @Test
    fun `loadAlbumDetail emite error si falla el repo`() = runTest {
        val vm = AlbumDetailViewModel(
            albumRepository = FakeAlbumRepo(null),
            trackRepository = FakeTrackRepo(),
            dispatcher = dispatcher
        )

        vm.loadAlbumDetail(1)
        advanceUntilIdle()

        assertNull(vm.album.value)
        assertEquals(
            "Error al obtener detalle del álbum: Album no encontrado",
            vm.error.value
        )
    }

    @Test
    fun `loading cambia de true a false durante la carga`() = runTest {
        val fakeAlbum = AlbumDto(
            id = 1,
            name = "X",
            cover = "c",
            releaseDate = "2020-01-01",
            description = "d",
            genre = "g",
            recordLabel = "l"
        )

        val delayedRepo = object : AlbumsService {
            override suspend fun getAlbums() = emptyList<AlbumDto>()
            override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
                delay(50)
                return fakeAlbum
            }

            override suspend fun createAlbum(albumBody: AlbumCreateDto): AlbumDto {
                TODO("Not yet implemented")
            }
        }

        val vm = AlbumDetailViewModel(
            AlbumRepository(delayedRepo),
            FakeTrackRepo(),
            dispatcher = dispatcher
        )

        assertFalse(vm.loading.value!!)

        vm.loadAlbumDetail(1)
        assertTrue(vm.loading.value!!)

        advanceUntilIdle()

        assertFalse(vm.loading.value!!)
    }

    @Test
    fun `addTrackToAlbum agrega y refresca tracks`() = runTest {
        val initialTrack = TrackDto(id = 1, name = "T1", duration = "4:00")

        val fakeTrackRepo = FakeTrackRepo(
            tracks = listOf(initialTrack)
        )

        val vm = AlbumDetailViewModel(
            FakeAlbumRepo(null),
            fakeTrackRepo,
            dispatcher = dispatcher
        )

        vm.loadTracks(1)
        advanceUntilIdle()

        assertEquals(1, vm.tracks.value?.size)

        vm.addTrackToAlbum(1, "Nueva", "3:00")
        advanceUntilIdle()

        // Se verifica que NO haya error y tracks no sea null
        assertNotNull(vm.tracks.value)
        assertNull(vm.error.value)
    }
}