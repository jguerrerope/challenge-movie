package com.jguerrerope.moviechanllenge.api

import com.jguerrerope.moviechanllenge.Configuration
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

class TMDBServiceTest {
    private lateinit var service: TMDBService
    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(IOException::class)
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create<TMDBService>(TMDBService::class.java)
    }

    @After
    @Throws(IOException::class)
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun success() {
        enqueueResponse("movie-list.json")
        service.getMoviePopular(page = 1)
                .test()
                .assertValue { it.page == 1 }
                .assertValue { it.results.size == Configuration.NUMBER_OF_ITEMS_PER_PAGE }
    }

    @Test
    fun badRequest() {
        mockWebServer.enqueue(MockResponse().setBody("{error:\"bad request\"").setResponseCode(400))
        service.getMoviePopular(page = -1)
                .test()
                .assertError(HttpException::class.java)
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String) {
        val inputStream =
                javaClass.classLoader.getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        mockWebServer.enqueue(MockResponse().setBody(source.readString(StandardCharsets.UTF_8)))
    }
}
