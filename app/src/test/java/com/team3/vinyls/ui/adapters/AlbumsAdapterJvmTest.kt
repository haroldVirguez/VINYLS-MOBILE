package com.team3.vinyls.ui.adapters

import android.os.Looper
import com.team3.vinyls.ui.models.AlbumUiModel
import com.team3.vinyls.testutils.TestImageLoader
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AlbumsAdapterJvmTest {

    @Test
    fun `submitList updates items and getItemCount reflects size`() {
        val adapter = AlbumsAdapter(imageLoader = TestImageLoader())
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

        val latch = CountDownLatch(1)
        adapter.submitList(items) { latch.countDown() }
        latch.await(1, TimeUnit.SECONDS)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `onAlbumClick callback can be registered and invoked`() {
        val adapter = AlbumsAdapter(imageLoader = TestImageLoader())
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
        val latch = CountDownLatch(1)
        adapter.submitList(listOf(item)) { latch.countDown() }
        latch.await(1, TimeUnit.SECONDS)
        shadowOf(Looper.getMainLooper()).idle()

        var clicked: AlbumUiModel? = null
        adapter.onAlbumClick = { clicked = it }

        // simulate what would happen in a ViewHolder: call the lambda directly
        adapter.onAlbumClick?.invoke(item)

        assertEquals(item, clicked)
    }
}
