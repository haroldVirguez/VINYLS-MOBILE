package com.team3.vinyls.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.team3.vinyls.data.AlbumsService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AlbumsServiceIntegrationTest {

    private lateinit var server: MockWebServer
    private lateinit var service: AlbumsService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        service = retrofit.create(AlbumsService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getAlbums returns parsed list`() {
        val json = """
            [
              {"id":1,"name":"Abbey Road","cover":"abbey_road.jpg","releaseDate":"1969-09-26","description":"The Beatles' final album","genre":"Rock","recordLabel":"Apple Records"},
              {"id":2,"name":"Revolver","cover":"revolver.jpg","releaseDate":"1966-08-05","description":"The Beatles' experimental album","genre":"Rock","recordLabel":"Parlophone"}
            ]
        """.trimIndent()

        server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        val result = runCatching { runBlockingFromTest { service.getAlbums() } }

        val list = result.getOrNull()
        assertEquals(2, list?.size)
        assertEquals("Abbey Road", list?.get(0)?.name)
    }

    // Helper to call suspend from test without bringing extra deps
    private fun <T> runBlockingFromTest(block: suspend () -> T): T = runBlocking { block() }
}

