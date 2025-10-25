package com.team3.vinyls.albums

import com.team3.vinyls.R
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationTest {
    @Test
    fun directionsContainAlbumIdArgument() {
        val action = com.team3.vinyls.albums.ui.fragments.AlbumsFragmentDirections
            .actionAlbumsFragmentToAlbumDetailFragment("99")

        assertEquals(R.id.action_albumsFragment_to_albumDetailFragment, action.actionId)

        val cls = action::class.java
        var albumId: String? = null

        val named = cls.declaredFields.firstOrNull { it.name == "albumId" }
        if (named != null) {
            named.isAccessible = true
            albumId = named.get(action) as? String
        } else {
            for (field in cls.declaredFields) {
                field.isAccessible = true
                val value = field.get(action)
                if (value is String) {
                    albumId = value
                    break
                }
            }
        }

        assertEquals("99", albumId)
    }
}
