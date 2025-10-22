package android.widget

import android.content.Context
import android.view.View

open class TextView(context: Context? = null) : View() {
    var text: CharSequence = ""
    // keep findViewById behavior from View
}

