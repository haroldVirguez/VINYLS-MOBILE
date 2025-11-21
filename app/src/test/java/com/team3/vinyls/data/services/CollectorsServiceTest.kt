package com.team3.vinyls.data.services

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CollectorsServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: CollectorsService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        service = retrofit.create(CollectorsService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getCollectors_returnsList() {
        val mockJson = """
            [
                {
                    "id": 1,
                    "name": "Jaime Andrés Monsalve",
                    "telephone": "3102178976",
                    "email": "j.monsalve@gmail.com",
                    "comments": [],
                    "favoritePerformers": [],
                    "collectorAlbums": []
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setBody(mockJson).setResponseCode(200)
        )

        val result = runBlocking { service.getCollectors() }

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Jaime Andrés Monsalve", result[0].name)
    }

    @Test
    fun getCollectorDetail_returnsObject() {
        val mockJson = """
            {
                "id": 5,
                "name": "María Alejandra Palacios",
                "telephone": "3502889087",
                "email": "j.palacios@outlook.es",
                "comments": [],
                "favoritePerformers": [],
                "collectorAlbums": []
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setBody(mockJson).setResponseCode(200)
        )

        val result = runBlocking { service.getCollectorDetail(5) }

        assertNotNull(result)
        assertEquals(5, result.id)
        assertEquals("María Alejandra Palacios", result.name)
    }

    @Test(expected = Exception::class)
    fun getCollectorDetail_failsOn404() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(404)
        )

        runBlocking {
            service.getCollectorDetail(999)
        }
    }
}

