package com.team3.vinyls.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.data.models.AlbumCreateDto
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.repositories.AlbumRepository
import com.team3.vinyls.data.services.AlbumsService
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

                override suspend fun getAlbumDetail(albumId: Int): AlbumDto {
                    throw NotImplementedError("getAlbumDetail test not implemented")
                }
                override suspend fun createAlbum(albumBody: AlbumCreateDto): AlbumDto {
                    TODO("Not yet implemented")
                }
            }

            val mockRepository = object : AlbumRepository(dummyService) {
                override suspend fun fetchAlbums(): List<AlbumDto> = listOf(
                    AlbumDto(1, "A", "B", "cover", "desc", "genre", "label")
                )
            }
            val viewModel = AlbumsViewModel(repository = mockRepository)

            // let coroutines in viewModelScope run
            advanceUntilIdle()

            Assert.assertEquals(1, viewModel.albums.value?.size)
            Assert.assertEquals("A", viewModel.albums.value?.first()?.title)
        } finally {
            Dispatchers.resetMain()
        }
    }
}