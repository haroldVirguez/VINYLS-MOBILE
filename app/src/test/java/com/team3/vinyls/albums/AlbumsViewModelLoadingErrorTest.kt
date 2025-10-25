package com.team3.vinyls.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.albums.data.AlbumRepository
import com.team3.vinyls.albums.data.AlbumsService
import com.team3.vinyls.albums.data.AlbumDto
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

class AlbumsViewModelLoadingErrorTest {
    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setsErrorWhenRepositoryThrows() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val dummyService = object : AlbumsService {
                override suspend fun getAlbums(): List<AlbumDto> = emptyList()

                override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
                    throw NotImplementedError("getAlbumDetail test not implemented")
                }
            }

            val mockRepository = object : AlbumRepository(dummyService) {
                override suspend fun fetchAlbums(): List<com.team3.vinyls.albums.ui.AlbumUiModel> {
                    throw RuntimeException("boom")
                }
            }
            val viewModel = AlbumsViewModel(repository = mockRepository)

            // let coroutines in viewModelScope run
            advanceUntilIdle()

            // After init, error should be populated
            assertEquals("boom", viewModel.error.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
