package com.team3.vinyls.albums.ui

import android.view.View
import android.widget.TextView
import com.team3.vinyls.R
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.*

class AlbumsAdapterMockitoTest {

    @Test
    fun bind_setsTextAndClickInvokesCallback() {
        val adapter = AlbumsAdapter()

        // Mocks for itemView and its child TextViews
        val itemView = mock<View>()
        val titleView = mock<TextView>()
        val subtitleView = mock<TextView>()

        // Stub findViewById to return our mocked TextViews
        whenever(itemView.findViewById<TextView>(R.id.txtTitle)).thenReturn(titleView)
        whenever(itemView.findViewById<TextView>(R.id.txtSubtitle)).thenReturn(subtitleView)

        // Create view holder using adapter's inner class
        val vh = adapter.AlbumViewHolder(itemView)

        val album = AlbumUiModel("1", "TitleX", "Artist • 2025")

        // Setup click callback collector
        var clicked: AlbumUiModel? = null
        adapter.onAlbumClick = { clicked = it }

        // Call bind
        vh.bind(album)

        // Verify setText called on TextViews
        verify(titleView).text = eq("TitleX")
        verify(subtitleView).text = eq("Artist • 2025")

        // Capture the OnClickListener set on itemView and invoke it
        val captor = argumentCaptor<View.OnClickListener>()
        verify(itemView).setOnClickListener(captor.capture())
        val listener = captor.firstValue
        // simulate click
        listener.onClick(itemView)

        // Verify callback invoked with our album
        assertNotNull(clicked)
        assertEquals("1", clicked?.id)
    }
}

