package com.team3.vinyls.core.network

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiConstantsTest {

    @Test
    fun `BASE_URL has correct value`() {
        assertEquals("https://backvynils-q6yc.onrender.com/", ApiConstants.BASE_URL)
    }

    @Test
    fun `ALBUMS_ENDPOINT has correct value`() {
        assertEquals("albums", ApiConstants.ALBUMS_ENDPOINT)
    }
}
