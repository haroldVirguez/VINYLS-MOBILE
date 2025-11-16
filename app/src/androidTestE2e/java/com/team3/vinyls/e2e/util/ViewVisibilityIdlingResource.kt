package com.team3.vinyls.e2e.util

import android.graphics.Rect
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource

/**
 * IdlingResource that becomes idle when a view with the given id is visible and
 * has a non-empty global visible rect (i.e., is actually laid out and visible on screen).
 *
 * Usage: create with your ActivityScenario and register with IdlingRegistry before assertions.
 */
class ViewVisibilityIdlingResource(
    private val scenario: ActivityScenario<*>,
    private val viewId: Int
) : IdlingResource {

    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "ViewVisibilityIdlingResource[$viewId]"

    override fun isIdleNow(): Boolean {
        var idle = false
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(viewId)
            if (view != null && view.visibility == View.VISIBLE) {
                val r = Rect()
                val hasRect = view.getGlobalVisibleRect(r)
                idle = hasRect && r.width() > 0 && r.height() > 0
            } else {
                idle = false
            }
        }
        if (idle) {
            callback?.onTransitionToIdle()
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}

