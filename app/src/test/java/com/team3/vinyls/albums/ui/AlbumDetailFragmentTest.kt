package com.team3.vinyls.albums.ui

import com.team3.vinyls.albums.ui.fragments.AlbumDetailFragment
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
        assert(fragment != null)
    }

    @Test
    fun `album detail fragment has correct class`() {
        assert(AlbumDetailFragment::class.java.simpleName == "AlbumDetailFragment")
    }
}
