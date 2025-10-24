package com.team3.vinyls.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NetworkModuleTest {

    @Test
    fun `retrofit creates instance with base URL`() {
        val baseUrl = "https://test.example.com/"
        val retrofit = NetworkModule.retrofit(baseUrl)
        
        assertNotNull(retrofit)
        assertEquals(baseUrl, retrofit.baseUrl().toString())
    }

    @Test
    fun `retrofit creates different instances for different URLs`() {
        val url1 = "https://test1.example.com/"
        val url2 = "https://test2.example.com/"
        
        val retrofit1 = NetworkModule.retrofit(url1)
        val retrofit2 = NetworkModule.retrofit(url2)
        
        assertNotNull(retrofit1)
        assertNotNull(retrofit2)
        assertEquals(url1, retrofit1.baseUrl().toString())
        assertEquals(url2, retrofit2.baseUrl().toString())
    }
}
