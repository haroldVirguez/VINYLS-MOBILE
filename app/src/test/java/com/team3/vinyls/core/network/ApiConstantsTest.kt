package com.team3.vinyls.core.network

import com.team3.vinyls.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiConstantsTest {

    @Test
    fun `BASE_URL has correct value`() {
        // Compare against generated BuildConfig for the active test variant so the test
        // works for prod/e2e flavors.
        assertEquals(BuildConfig.BASE_URL, ApiConstants.BASE_URL)
    }

    @Test
    fun `ALBUMS_ENDPOINT has correct value`() {
        assertEquals("albums", ApiConstants.ALBUMS_ENDPOINT)
    }
}
