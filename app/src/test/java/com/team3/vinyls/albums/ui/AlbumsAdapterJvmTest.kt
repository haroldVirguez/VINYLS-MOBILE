package com.team3.vinyls.albums.ui

import com.team3.vinyls.albums.ui.adapters.AlbumsAdapter
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumsAdapterJvmTest {

    @Test
    fun `submitList updates items and getItemCount reflects size`() {
        val adapter = AlbumsAdapter()
        val items = listOf(
            AlbumUiModel(id = "1", title = "T1", subtitle = "S1"),
            AlbumUiModel(id = "2", title = "T2", subtitle = "S2")
        )

        adapter.submitList(items)

        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `onAlbumClick callback can be registered and invoked`() {
        val adapter = AlbumsAdapter()
        val item = AlbumUiModel(id = "1", title = "T1", subtitle = "S1")
        adapter.submitList(listOf(item))

        var clicked: AlbumUiModel? = null
        adapter.onAlbumClick = { clicked = it }

        // simulate what would happen in a ViewHolder: call the lambda directly
        adapter.onAlbumClick?.invoke(item)

        assertEquals(item, clicked)
    }
}

