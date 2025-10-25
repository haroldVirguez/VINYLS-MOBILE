package com.team3.vinyls.albums.ui.adapters

import com.team3.vinyls.albums.ui.AlbumUiModel
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumsAdapterTest {

    @Test
    fun `submitList updates items and getItemCount reflects size`() {
        val adapter = AlbumsAdapter()
        val items = listOf(
            AlbumUiModel(
                id = 1,
                title = "T1",
                subtitle = "S1",
                cover = "cover1",
                description = "desc1",
                genre = "genre1",
                recordLabel = "label1",
                releaseDate = "2023-01-01"
            ),
            AlbumUiModel(
                id = 2,
                title = "T2",
                subtitle = "S2",
                cover = "cover2",
                description = "desc2",
                genre = "genre2",
                recordLabel = "label2",
                releaseDate = "2023-01-02"
            )
        )

        adapter.submitList(items)

        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `submitList replaces previous items`() {
        val adapter = AlbumsAdapter()
        adapter.submitList(listOf(
            AlbumUiModel(
                1,
                "A",
                "a",
                "cover",
                "desc",
                "genre",
                "label",
                "2023-01-01"
            )
        ))
        assertEquals(1, adapter.itemCount)

        adapter.submitList(emptyList())
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `onAlbumClick callback can be registered and invoked`() {
        val adapter = AlbumsAdapter()
        val item = AlbumUiModel(
            id = 1,
            title = "T1",
            subtitle = "S1",
            cover = "cover",
            description = "desc",
            genre = "genre",
            recordLabel = "label",
            releaseDate = "2023-01-01"
        )
        adapter.submitList(listOf(item))

        var clicked: AlbumUiModel? = null
        adapter.onAlbumClick = { clicked = it }

        // simulate what would happen in a ViewHolder: call the lambda directly
        adapter.onAlbumClick?.invoke(item)

        assertEquals(item, clicked)
    }
}
