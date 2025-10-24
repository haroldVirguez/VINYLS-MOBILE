package com.team3.vinyls.albums

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.team3.vinyls.core.network.AlbumsService
import com.team3.vinyls.albums.data.AlbumRepository

class AlbumRepositoryTest {
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun mapsDtoToUiModel() {
        val body = """
            [
              {"id":"1","name":"Test","artist":"Artist","year":2020}
            ]
        """.trimIndent()
        server.enqueue(MockResponse().setBody(body).setResponseCode(200))

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/").toString())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val service = retrofit.create(AlbumsService::class.java)
        val repo = AlbumRepository(service)

        val result = kotlinx.coroutines.runBlocking { repo.fetchAlbums() }

        assertEquals(1, result.size)
        assertEquals("Test", result[0].title)
        assertEquals("Artist • 2020", result[0].subtitle)
    }
}
