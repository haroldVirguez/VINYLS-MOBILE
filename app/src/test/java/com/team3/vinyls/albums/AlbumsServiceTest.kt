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
import com.team3.vinyls.albums.data.AlbumsService

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
              {"id":"1","name":"Test","artist":"Artist","year":2020},
              {"id":"2","name":"Another","artist":"B","year":2021}
            ]
        """.trimIndent()
        server.enqueue(MockResponse().setBody(body).setResponseCode(200))

        val list = kotlinx.coroutines.runBlocking { service.getAlbums() }

        assertEquals(2, list.size)
        assertEquals("Test", list[0].name)
    }
}
