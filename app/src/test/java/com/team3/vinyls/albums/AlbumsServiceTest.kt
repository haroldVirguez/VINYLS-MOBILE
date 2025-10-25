package com.team3.vinyls.albums

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.team3.vinyls.core.network.AlbumsService

class AlbumsServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: AlbumsService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        service = Retrofit.Builder()
            .baseUrl(server.url("/").toString())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AlbumsService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun getAlbums_returnsList() {
        val body = """
            [
              {"id":1,"name":"Test","cover":"cover1.jpg","releaseDate":"2020-01-01","description":"Test album","genre":"Rock","recordLabel":"Test Label"},
              {"id":2,"name":"Another","cover":"cover2.jpg","releaseDate":"2021-01-01","description":"Another album","genre":"Pop","recordLabel":"Another Label"}
            ]
        """.trimIndent()
        server.enqueue(MockResponse().setBody(body).setResponseCode(200))

        val list = kotlinx.coroutines.runBlocking { service.getAlbums() }

        assertEquals(2, list.size)
        assertEquals("Test", list[0].name)
    }
}
