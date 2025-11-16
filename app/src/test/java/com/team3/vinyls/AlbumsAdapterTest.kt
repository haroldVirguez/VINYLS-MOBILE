package com.team3.vinyls

import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import com.team3.vinyls.ui.models.AlbumUiModel
import com.team3.vinyls.ui.adapters.AlbumsAdapter
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

class AlbumsAdapterTest {

    @Test
    fun bindAndClickInvokesCallback() {
        val adapter = AlbumsAdapter(StandardTestDispatcher())

        // Crear vistas reales en el contexto de Robolectric para evitar problemas de classloader con Glide
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

        // Dejar cover vacío para evitar llamar a Glide.with en tests
        val album = AlbumUiModel(42, "T", "S", "", "desc", "genre", "label", "2023-01-01")

        var clickedId: Int? = null
        adapter.onAlbumClick = { clickedId = it.id }

        vh.bind(album)

        // Simular click en la vista real
        itemView.performClick()

        assertNotNull(clickedId)
        assertEquals(42, clickedId)
    }
}
