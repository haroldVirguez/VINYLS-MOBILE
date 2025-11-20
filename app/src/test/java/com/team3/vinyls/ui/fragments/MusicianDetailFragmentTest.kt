package com.team3.vinyls.ui.fragments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MusicianDetailFragmentTest {

    @Test
    fun `musician detail fragment can be instantiated`() {
        val fragment = MusicianDetailFragment()
        assertNotNull(fragment)
    }

    @Test
    fun `musician detail fragment has correct class`() {
        assertEquals("MusicianDetailFragment", MusicianDetailFragment::class.java.simpleName)
    }
}