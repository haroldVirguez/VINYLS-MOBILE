package com.team3.vinyls

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import com.team3.vinyls.data.services.MusiciansService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class MusiciansServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: MusiciansService

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

        service = retrofit.create(MusiciansService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getMusicians_returnsList() {
        val mockJson = """
            [
                {
                    "id": 1,
                    "name": "Rubén Blades",
                    "image": "img.jpg",
                    "description": "Cantante",
                    "birthDate": "1948-07-16",
                    "albums": [],
                    "performerPrizes": []
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setBody(mockJson).setResponseCode(200)
        )

        val result = runBlocking { service.getMusicians() }

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Rubén Blades", result[0].name)
    }

    @Test
    fun getMusicianDetail_returnsObject() {
        val mockJson = """
            {
                "id": 5,
                "name": "Joan Manuel Serrat",
                "image": "serrrat.jpg",
                "description": "Cantautor",
                "birthDate": "1943-12-27",
                "albums": [],
                "performerPrizes": []
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setBody(mockJson).setResponseCode(200)
        )

        val result = runBlocking { service.getMusicianDetail(5) }

        assertNotNull(result)
        assertEquals(5, result.id)
        assertEquals("Joan Manuel Serrat", result.name)
    }

    @Test(expected = Exception::class)
    fun getMusicianDetail_failsOn404() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(404)
        )

        runBlocking {
            service.getMusicianDetail(999)
        }
    }
}