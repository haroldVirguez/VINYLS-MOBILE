package com.team3.vinyls.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.AlbumRepository
import com.team3.vinyls.data.AlbumsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
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
                override suspend fun fetchAlbums(): List<AlbumDto> {
                    throw RuntimeException("boom")
                }
            }
            val viewModel = AlbumsViewModel(repository = mockRepository)

            // let coroutines in viewModelScope run
            advanceUntilIdle()

            // After init, error should be populated
            Assert.assertEquals("boom", viewModel.error.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}