package com.team3.vinyls

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SecondFragmentTest {

    @Test
    fun `second fragment can be instantiated`() {
        val fragment = SecondFragment()
        assert(fragment != null)
    }

    @Test
    fun `second fragment has correct class`() {
        assert(SecondFragment::class.java.simpleName == "SecondFragment")
    }
}
