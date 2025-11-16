package com.team3.vinyls.ui.adapters

import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.team3.vinyls.R
import com.team3.vinyls.ui.models.MusicianUiModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.Dispatchers
import com.team3.vinyls.testutils.TestImageLoader
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.robolectric.Shadows.shadowOf
import android.os.Looper

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MusiciansAdapterTest {

    @Test
    fun `submitList updates itemCount and bind sets texts and placeholder image`() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val parent = FrameLayout(context)
        val adapter = MusiciansAdapter(uiDispatcher = Dispatchers.Unconfined, imageLoader = TestImageLoader())

        val holder = adapter.onCreateViewHolder(parent, 0)
        val item = MusicianUiModel(id = 1, name = "Artist", subtitle = "Rock", image = null)

        val latch = CountDownLatch(1)
        adapter.submitList(listOf(item)) { latch.countDown() }
        latch.await(1, TimeUnit.SECONDS)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(1, adapter.itemCount)

        adapter.onBindViewHolder(holder, 0)

        val nameView = holder.itemView.findViewById<android.widget.TextView>(R.id.txtName)
        val subtitleView = holder.itemView.findViewById<android.widget.TextView>(R.id.txtSubtitle)
        val imgView = holder.itemView.findViewById<android.widget.ImageView>(R.id.imgArtist)

        assertEquals("Artist", nameView.text.toString())
        assertEquals("Rock", subtitleView.text.toString())

        // TestImageLoader sets drawable to null in tests
        assertNull(imgView.drawable)
    }

    @Test
    fun `click on item invokes onMusicianClick`() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val parent = FrameLayout(context)
        val adapter = MusiciansAdapter(uiDispatcher = Dispatchers.Unconfined, imageLoader = TestImageLoader())

        val holder = adapter.onCreateViewHolder(parent, 0)
        val item = MusicianUiModel(id = 2, name = "ClickMe", subtitle = "Pop", image = null)

        var clicked: MusicianUiModel? = null
        adapter.onMusicianClick = { clicked = it }

        val latch = CountDownLatch(1)
        adapter.submitList(listOf(item)) { latch.countDown() }
        latch.await(1, TimeUnit.SECONDS)
        shadowOf(Looper.getMainLooper()).idle()

        adapter.onBindViewHolder(holder, 0)

        holder.itemView.performClick()

        assertNotNull(clicked)
        assertEquals("ClickMe", clicked!!.name)
    }
}