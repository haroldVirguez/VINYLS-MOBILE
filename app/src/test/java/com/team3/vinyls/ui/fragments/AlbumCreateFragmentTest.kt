package com.team3.vinyls.ui.fragments

import android.widget.EditText
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import com.team3.vinyls.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AlbumCreateFragmentTest {
    @Test
    fun `album create fragment can be instantiated`() {
        val fragment = AlbumCreateFragment()
        assertNotNull(fragment)
    }

    @Test
    fun `album create fragment has correct class`() {
        assertEquals("AlbumCreateFragment", AlbumCreateFragment::class.java.simpleName)
    }
}