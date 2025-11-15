package com.team3.vinyls

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.team3.vinyls.ui.models.AlbumUiModel
import com.team3.vinyls.ui.adapters.AlbumsAdapter
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import android.content.Context
import com.bumptech.glide.RequestManager
import org.mockito.Answers

class AlbumsAdapterTest {

    @Test
    fun bindAndClickInvokesCallback() {
        val adapter = AlbumsAdapter(StandardTestDispatcher())

        val itemView = mock<View>()
        val titleView = mock<TextView>()
        val subtitleView = mock<TextView>()
        val genreView = mock<TextView>()
        val imCover = mock<ImageView>()

        whenever(itemView.findViewById<TextView>(R.id.txtTitle)).thenReturn(titleView)
        whenever(itemView.findViewById<TextView>(R.id.txtSubtitle)).thenReturn(subtitleView)
        whenever(itemView.findViewById<TextView>(R.id.txtGenre)).thenReturn(genreView)
        whenever(itemView.findViewById<ImageView>(R.id.imgCover)).thenReturn(imCover)
        whenever(itemView.context).thenReturn(mock<Context>())

        // Mockear Glide
        val glideMock = Mockito.mockStatic(Glide::class.java)
        val requestManager = mock<RequestManager>(defaultAnswer = Answers.RETURNS_DEEP_STUBS)
        glideMock.`when`<RequestManager> {
            Glide.with(Mockito.any(Context::class.java))
        }.thenReturn(requestManager)

        val vh = adapter.AlbumViewHolder(itemView)

        val album = AlbumUiModel(42, "T", "S", "cover", "desc", "genre", "label", "2023-01-01")

        var clickedId: Int? = null
        adapter.onAlbumClick = { clickedId = it.id }

        vh.bind(album)

        val captor = argumentCaptor<View.OnClickListener>()
        verify(itemView).setOnClickListener(captor.capture())
        val listener = captor.firstValue
        listener.onClick(itemView)

        assertNotNull(clickedId)
        assertEquals(42, clickedId)

        glideMock.close()
    }
}
