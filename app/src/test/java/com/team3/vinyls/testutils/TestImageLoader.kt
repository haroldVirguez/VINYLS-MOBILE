package com.team3.vinyls.testutils

import android.widget.ImageView
import com.team3.vinyls.ui.adapters.ImageLoader

/**
 * Test-friendly ImageLoader that does nothing (no Glide calls) to keep unit tests isolated.
 */
class TestImageLoader : ImageLoader {
    override fun load(url: String?, target: ImageView) {
        // No-op: in tests we don't need to load images
        target.setImageDrawable(null)
    }

    override fun clear(target: ImageView) {
        // No-op
        target.setImageDrawable(null)
    }
}

