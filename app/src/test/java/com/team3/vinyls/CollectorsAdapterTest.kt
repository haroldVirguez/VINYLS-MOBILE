package com.team3.vinyls

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.team3.vinyls.ui.adapters.CollectorsAdapter
import com.team3.vinyls.ui.models.CollectorUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class CollectorsAdapterTest {

    @Test
    fun bindAndClickInvokesCallback() {
        val adapter = CollectorsAdapter(StandardTestDispatcher())

        val context = ApplicationProvider.getApplicationContext<Context>()
        val itemView = LinearLayout(context)

        val nameView = TextView(context)
        nameView.id = R.id.txtName
        itemView.addView(nameView)

        val subtitleView = TextView(context)
        subtitleView.id = R.id.txtSubtitle
        itemView.addView(subtitleView)

        val imgView = ImageView(context)
        imgView.id = R.id.imgCollector
        itemView.addView(imgView)

        val vh = adapter.CollectorViewHolder(itemView)

        val collector = CollectorUiModel(7, "C1", "Bogotá", null)

        var clickedId: Int? = null
        adapter.onCollectorClick = { clickedId = it.id }

        vh.bind(collector)

        itemView.performClick()

        assertNotNull(clickedId)
        assertEquals(7, clickedId)
    }
}

