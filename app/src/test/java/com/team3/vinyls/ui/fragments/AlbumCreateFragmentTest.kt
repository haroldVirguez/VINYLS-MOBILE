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

    @Test
    fun `album create fragment renders its fields and are writable`() {
        val scenario = launchFragmentInContainer<AlbumCreateFragment>(
            themeResId = R.style.Theme_Vinyls
        )

        scenario.onFragment { fragment ->
            val view = fragment.requireView()

            val nameField = view.findViewById<EditText>(R.id.inputTitle)
            val coverField = view.findViewById<EditText>(R.id.inputCoverUrl)
            val releaseDateField = view.findViewById<EditText>(R.id.inputReleaseDate)
            val descriptionField = view.findViewById<EditText>(R.id.inputDescription)
            val genreField = view.findViewById<EditText>(R.id.inputGenre)
            val recordLabelField = view.findViewById<EditText>(R.id.inputLabel)

            assertNotNull(nameField)
            assertNotNull(coverField)
            assertNotNull(releaseDateField)
            assertNotNull(descriptionField)
            assertNotNull(genreField)
            assertNotNull(recordLabelField)

            // write values
            nameField.setText("Test Album")
            coverField.setText("test_cover.jpg")
            releaseDateField.setText("2023-01-01")
            descriptionField.setText("This is a test album.")
            genreField.setText("Rock")
            recordLabelField.setText("Test Label")

            // verify input
            assertEquals("Test Album", nameField.text.toString())
            assertEquals("test_cover.jpg", coverField.text.toString())
            assertEquals("2023-01-01", releaseDateField.text.toString())
            assertEquals("This is a test album.", descriptionField.text.toString())
            assertEquals("Rock", genreField.text.toString())
            assertEquals("Test Label", recordLabelField.text.toString())
        }
    }
}