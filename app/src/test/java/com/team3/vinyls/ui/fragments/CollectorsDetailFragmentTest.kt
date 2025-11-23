package com.team3.vinyls.ui.fragments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CollectorsDetailFragmentTest {

    @Test
    fun `collectors detail fragment can be instantiated`() {
        val fragment = CollectorsDetailFragment()
        assertNotNull(fragment)
    }

    @Test
    fun `collectors detail fragment has correct class`() {
        assertEquals("CollectorsDetailFragment", CollectorsDetailFragment::class.java.simpleName)
    }
}

