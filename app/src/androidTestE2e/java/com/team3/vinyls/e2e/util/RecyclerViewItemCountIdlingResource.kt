package com.team3.vinyls.e2e.util

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingResource

class RecyclerViewItemCountIdlingResource(
    private val recyclerView: RecyclerView,
    private val minItemCount: Int = 1
) : IdlingResource {

    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    // Track last idle state to avoid duplicate callbacks
    private var lastIdleState: Boolean = false

    // Keep reference to the adapter the observer is registered to
    private var registeredAdapter: RecyclerView.Adapter<*>? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = checkIdle()
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = checkIdle()
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = checkIdle()
    }

    init {
        // Register to the current adapter if present
        registeredAdapter = recyclerView.adapter
        registeredAdapter?.registerAdapterDataObserver(observer)
    }

    override fun getName(): String = "RecyclerViewItemCountIdlingResource(${recyclerView.id})"

    override fun isIdleNow(): Boolean {
        val isIdle = (recyclerView.adapter?.itemCount ?: 0) >= minItemCount
        // Update lastIdleState and notify only on transition from non-idle -> idle
        if (isIdle && !lastIdleState) {
            lastIdleState = true
            resourceCallback?.onTransitionToIdle()
        } else if (!isIdle) {
            lastIdleState = false
        }
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
        // Ensure observer is registered to the current adapter (handle adapter swap)
        updateAdapterObserver()
        // Check current state and notify if already idle
        checkIdle()
    }

    /**
     * Unregister observer and clear callback to avoid leaks. Call this from test teardown.
     */
    @Suppress("unused")
    fun unregister() {
        registeredAdapter?.unregisterAdapterDataObserver(observer)
        registeredAdapter = null
        resourceCallback = null
    }

    private fun updateAdapterObserver() {
        val current = recyclerView.adapter
        if (registeredAdapter === current) return
        // Unregister from previous adapter
        registeredAdapter?.unregisterAdapterDataObserver(observer)
        // Register to new adapter if present
        current?.registerAdapterDataObserver(observer)
        registeredAdapter = current
    }

    private fun checkIdle() {
        // If adapter changed, rewire observer
        updateAdapterObserver()
        // Delegate to isIdleNow which will notify only on transition
        isIdleNow()
    }
}
