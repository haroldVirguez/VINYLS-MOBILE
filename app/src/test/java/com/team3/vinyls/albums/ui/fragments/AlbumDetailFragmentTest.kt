package com.team3.vinyls.albums.ui.fragments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AlbumDetailFragmentTest {

    @Test
    fun `album detail fragment can be instantiated`() {
        val fragment = AlbumDetailFragment()
        assertNotNull(fragment)
    }

    @Test
    fun `album detail fragment has correct class`() {
        assertEquals("AlbumDetailFragment", AlbumDetailFragment::class.java.simpleName)
    }
}