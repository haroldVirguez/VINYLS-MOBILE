package com.team3.vinyls.ui.adapters

import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.team3.vinyls.R
import com.team3.vinyls.ui.MusicianUiModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.Dispatchers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MusiciansAdapterTest {

    @Test
    fun `submitList updates itemCount and bind sets texts and placeholder image`() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val parent = FrameLayout(context)
        val adapter = MusiciansAdapter(uiDispatcher = Dispatchers.Unconfined)

        val holder = adapter.onCreateViewHolder(parent, 0)
        val item = MusicianUiModel(id = 1, name = "Artist", subtitle = "Rock", image = null)

        adapter.submitList(listOf(item))
        assertEquals(1, adapter.itemCount)

        // Bind should set texts and placeholder image (no remote load since image == null)
        adapter.onBindViewHolder(holder, 0)

        val nameView = holder.itemView.findViewById<android.widget.TextView>(R.id.txtName)
        val subtitleView = holder.itemView.findViewById<android.widget.TextView>(R.id.txtSubtitle)
        val imgView = holder.itemView.findViewById<android.widget.ImageView>(R.id.imgArtist)

        assertEquals("Artist", nameView.text.toString())
        assertEquals("Rock", subtitleView.text.toString())
        assertNotNull(imgView.drawable)
    }

    @Test
    fun `click on item invokes onMusicianClick`() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val parent = FrameLayout(context)
        val adapter = MusiciansAdapter(uiDispatcher = Dispatchers.Unconfined)

        val holder = adapter.onCreateViewHolder(parent, 0)
        val item = MusicianUiModel(id = 2, name = "ClickMe", subtitle = "Pop", image = null)

        var clicked: MusicianUiModel? = null
        adapter.onMusicianClick = { clicked = it }

        adapter.submitList(listOf(item))
        adapter.onBindViewHolder(holder, 0)

        // perform click on root
        holder.itemView.performClick()

        assertNotNull(clicked)
        assertEquals("ClickMe", clicked!!.name)
    }
}
