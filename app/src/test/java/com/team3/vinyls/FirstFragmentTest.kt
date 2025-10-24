package com.team3.vinyls

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FirstFragmentTest {

    @Test
    fun `first fragment can be instantiated`() {
        val fragment = FirstFragment()
        assert(fragment != null)
    }

    @Test
    fun `first fragment has correct class`() {
        assert(FirstFragment::class.java.simpleName == "FirstFragment")
    }
}
