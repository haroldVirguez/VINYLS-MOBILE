package com.team3.vinyls.e2e.util

import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource

/**
 * IdlingResource that becomes idle when the view with the given id has at least [minChildCount] children.
 * Useful to wait until dynamic content (e.g. tracks) is added to a container.
 */
class ViewChildCountIdlingResource(
    private val scenario: ActivityScenario<*>,
    private val viewId: Int,
    private val minChildCount: Int = 1
) : IdlingResource {

    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "ViewChildCountIdlingResource[$viewId]"

    override fun isIdleNow(): Boolean {
        var idle = false
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(viewId)
            if (view is ViewGroup) {
                idle = view.childCount >= minChildCount
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

