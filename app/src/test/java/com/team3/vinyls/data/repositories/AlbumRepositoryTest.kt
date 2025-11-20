package com.team3.vinyls.data.repositories

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.team3.vinyls.data.services.AlbumsService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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
    fun getsInfoFromService() {
        val body = """
            [
              {"id":1,"name":"Test","cover":"cover.jpg","releaseDate":"2020-01-01","description":"Test album","genre":"Rock","recordLabel":"Test Label"}
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

        val result = runBlocking { repo.fetchAlbums() }

        Assert.assertEquals(1, result.size)
        Assert.assertEquals("Test", result[0].name)

    }
}