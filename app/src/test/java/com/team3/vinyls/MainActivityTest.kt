package com.team3.vinyls

import android.os.Bundle
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MainActivityTest {

    @Test
    fun `main activity can be instantiated`() {
        val activity = MainActivity()
        assert(activity != null)
    }

    @Test
    fun `main activity has correct class`() {
        assert(MainActivity::class.java.simpleName == "MainActivity")
    }
}
