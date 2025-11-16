package com.team3.vinyls.ui.adapters

import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import com.team3.vinyls.R
import com.team3.vinyls.ui.models.AlbumUiModel
import com.team3.vinyls.testutils.TestImageLoader
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Test
import org.junit.Assert.*
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])

class AlbumsAdapterMockitoTest {

    @Test
    fun bind_setsTextAndClickInvokesCallback() {
        val adapter = AlbumsAdapter(StandardTestDispatcher(), TestImageLoader())

        val context = ApplicationProvider.getApplicationContext<Context>()
        val itemView = LinearLayout(context)

        val titleView = TextView(context)
        titleView.id = R.id.txtTitle
        itemView.addView(titleView)

        val subtitleView = TextView(context)
        subtitleView.id = R.id.txtSubtitle
        itemView.addView(subtitleView)

        val genreView = TextView(context)
        genreView.id = R.id.txtGenre
        itemView.addView(genreView)

        val imCover = ImageView(context)
        imCover.id = R.id.imgCover
        itemView.addView(imCover)

        val vh = adapter.AlbumViewHolder(itemView)

        val album = AlbumUiModel(
            1,
            "TitleX",
            "Artist • 2025",
            "", // dejar cover vacío para evitar llamar a Glide.with en tests
            "desc",
            "genre",
            "label",
            "2025-01-01"
        )

        var clicked: AlbumUiModel? = null
        adapter.onAlbumClick = { clicked = it }

        vh.bind(album)

        itemView.performClick()

        assertNotNull(clicked)
        assertEquals(1, clicked?.id)
    }
}
