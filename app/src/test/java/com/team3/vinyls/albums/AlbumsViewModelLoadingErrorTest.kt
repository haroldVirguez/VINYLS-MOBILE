package com.team3.vinyls.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.albums.data.AlbumRepository
import com.team3.vinyls.core.network.AlbumsService
import com.team3.vinyls.core.network.AlbumDto
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
            }

            val viewModel = AlbumsViewModel(repositoryFactory = {
                object : AlbumRepository(dummyService) {
                    override suspend fun fetchAlbums(): List<AlbumUiModel> {
                        throw RuntimeException("boom")
                    }
                }
            })

            // let coroutines in viewModelScope run
            advanceUntilIdle()

            // After init, error should be populated
            assertEquals("boom", viewModel.error.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
