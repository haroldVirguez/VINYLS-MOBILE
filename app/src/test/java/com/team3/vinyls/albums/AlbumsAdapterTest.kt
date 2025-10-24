package com.team3.vinyls.albums

import android.view.View
import android.widget.TextView
import com.team3.vinyls.albums.ui.AlbumUiModel
import com.team3.vinyls.albums.ui.AlbumsAdapter
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AlbumsAdapterTest {

    @Test
    fun bindAndClickInvokesCallback() {
        val adapter = AlbumsAdapter()

        // Mocks for itemView and its child TextViews
        val itemView = mock<View>()
        val titleView = mock<TextView>()
        val subtitleView = mock<TextView>()

        // Stub findViewById to return our mocked TextViews
        whenever(itemView.findViewById<TextView>(com.team3.vinyls.R.id.txtTitle)).thenReturn(titleView)
        whenever(itemView.findViewById<TextView>(com.team3.vinyls.R.id.txtSubtitle)).thenReturn(subtitleView)

        // Create view holder using adapter's inner class
        val vh = adapter.AlbumViewHolder(itemView)

        val album = AlbumUiModel("42", "T", "S")

        // Setup click callback collector
        var clickedId: String? = null
        adapter.onAlbumClick = { clickedId = it.id }

        // Call bind
        vh.bind(album)

        // Capture the OnClickListener set on itemView and invoke it
        val captor = argumentCaptor<View.OnClickListener>()
        verify(itemView).setOnClickListener(captor.capture())
        val listener = captor.firstValue
        // simulate click
        listener.onClick(itemView)

        // Verify callback invoked with our album id
        assertNotNull(clickedId)
        assertEquals("42", clickedId)
    }
}
