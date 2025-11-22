package com.team3.vinyls.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.models.MusicianDto
import com.team3.vinyls.data.models.PerformerPrizeDto
import com.team3.vinyls.data.repositories.MusicianRepository
import com.team3.vinyls.ui.fragments.AlbumDetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class MusiciansDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: MusicianRepository

    @Mock
    private lateinit var observer: Observer<MusicianDto?>

    private lateinit var viewModel: MusiciansDetailViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)

        viewModel = MusiciansDetailViewModel(repository)
        viewModel.musician.observeForever(observer)
    }

    @Test
    fun `loadMusicianDetail should post musician data`() = runTest {
        val mockMusician = MusicianDto(
            id = 1,
            name = "Rubén Blades",
            image = "image.jpg",
            description = "Descripción",
            birthDate = "1948-07-16",
            albums = listOf(
                AlbumDto(
                    id = 1,
                    name = "Album 1",
                    cover = "cover.jpg",
                    releaseDate = "1984-08-01",
                    description = "desc",
                    genre = "Salsa",
                    recordLabel = "Fania",
                    tracks = emptyList(),
                    performers = emptyList(),
                    comments = emptyList()
                )
            ),
            performerPrizes = listOf(PerformerPrizeDto(1, "2020-01-01"))
        )

        `when`(repository.fetchMusicianDetail(1)).thenReturn(mockMusician)

        viewModel.loadMusicianDetail(1)
        dispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(mockMusician)
    }

    @Test
    fun `loadMusicianDetail should handle null response`() = runTest {
        `when`(repository.fetchMusicianDetail(99)).thenReturn(null)

        viewModel.loadMusicianDetail(99)
        dispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(null)
    }


    @Test
    fun `album detail fragment simple name is correct`() {
        val fragment = AlbumDetailFragment()
        assertEquals("AlbumDetailFragment", fragment::class.java.simpleName)
    }

    @Test
    fun `album detail fragment class is not null`() {
        val fragmentClass = AlbumDetailFragment::class.java
        assertNotNull(fragmentClass)
    }
}