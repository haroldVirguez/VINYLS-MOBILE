package com.team3.vinyls

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team3.vinyls.data.models.CollectorDto
import com.team3.vinyls.data.repositories.CollectorRepository
import com.team3.vinyls.viewmodels.CollectorsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CollectorsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository: CollectorRepository = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadCollectors_success_updatesLiveData() = runTest {
        val dtoList = listOf(
            CollectorDto(2, "Beta", "300", "b@example.com", null, null, null),
            CollectorDto(1, "Alpha", "301", "a@example.com", null, null, null)
        )

        whenever(repository.fetchCollectors()).thenReturn(dtoList)

        val vm = CollectorsViewModel(repository)
        // avanzar corrutinas
        testDispatcher.scheduler.advanceUntilIdle()

        val result = vm.collectors.value
        assertNotNull(result)
        // debe venir ordenado por name (Alpha, Beta)
        assertEquals("Alpha", result!![0].name)
        assertEquals("Beta", result[1].name)
    }

    @Test
    fun loadCollectors_error_setsError() = runTest {
        whenever(repository.fetchCollectors()).thenThrow(RuntimeException("fail"))

        val vm = CollectorsViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val err = vm.error.value
        assertNotNull(err)
    }
}
