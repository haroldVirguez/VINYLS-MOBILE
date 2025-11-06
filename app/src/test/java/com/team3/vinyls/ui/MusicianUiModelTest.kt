package com.team3.vinyls.ui

import org.junit.Assert.*
import org.junit.Test

class MusicianUiModelTest {

    @Test
    fun `data class equality and copy works`() {
        val m1 = MusicianUiModel(id = 1, name = "Name", subtitle = "Sub", image = "img")
        val m2 = MusicianUiModel(id = 1, name = "Name", subtitle = "Sub", image = "img")
        assertEquals(m1, m2)

        val m3 = m1.copy(id = 2)
        assertNotEquals(m1, m3)
        assertEquals(2, m3.id)
    }
}

