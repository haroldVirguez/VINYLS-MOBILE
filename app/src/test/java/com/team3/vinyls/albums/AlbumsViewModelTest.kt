package com.team3.vinyls.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.albums.data.AlbumRepository
import com.team3.vinyls.albums.data.AlbumsService
import com.team3.vinyls.albums.data.AlbumDto
import com.team3.vinyls.albums.ui.AlbumUiModel
import com.team3.vinyls.albums.viewmodels.AlbumsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AlbumsViewModelTest {
    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun emitsSuccessFromRepository() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            // Provide a non-null dummy AlbumsService to satisfy AlbumRepository constructor
            val dummyService = object : AlbumsService {
                override suspend fun getAlbums(): List<AlbumDto> = emptyList()
            }

            val mockRepository = object : AlbumRepository(dummyService) {
                override suspend fun fetchAlbums(): List<AlbumUiModel> = listOf(
                    AlbumUiModel(1, "A", "B", "cover", "desc", "genre", "label", "2023-01-01")
                )
            }
            val viewModel = AlbumsViewModel(repository = mockRepository)

            // let coroutines in viewModelScope run
            advanceUntilIdle()

            assertEquals(1, viewModel.albums.value?.size)
            assertEquals("A", viewModel.albums.value?.first()?.title)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
