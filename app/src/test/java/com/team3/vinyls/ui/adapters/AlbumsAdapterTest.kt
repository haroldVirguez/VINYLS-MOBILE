package com.team3.vinyls.ui.adapters

import com.team3.vinyls.ui.models.AlbumUiModel
import com.team3.vinyls.testutils.TestImageLoader
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import android.os.Looper
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])

class AlbumsAdapterTest {

    @Test
    fun `submitList updates items and getItemCount reflects size`() {
        val adapter = AlbumsAdapter(StandardTestDispatcher(), TestImageLoader())
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
        // wait for the async diff to finish and commit on the main thread
        latch.await(1, TimeUnit.SECONDS)
        // ensure any queued runnables (e.g. from image loading or Recycler internals) are processed
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `submitList replaces previous items`() {
        val adapter = AlbumsAdapter(imageLoader = TestImageLoader())

        val latch1 = CountDownLatch(1)
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
        )) { latch1.countDown() }
        latch1.await(1, TimeUnit.SECONDS)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, adapter.itemCount)

        val latch2 = CountDownLatch(1)
        adapter.submitList(emptyList()) { latch2.countDown() }
        latch2.await(1, TimeUnit.SECONDS)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(0, adapter.itemCount)
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
