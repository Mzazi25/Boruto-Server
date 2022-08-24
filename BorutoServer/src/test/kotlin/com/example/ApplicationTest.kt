package com.example

import com.example.models.ApiResponse
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*
import com.example.repository.HeroRepository
import io.ktor.client.call.*
import io.ktor.util.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

class ApplicationTest {
    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)
    @Test
    fun `access root endpoint, assert correct information`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            assertEquals(
                expected = "Welcome to Boruto API",
                actual = bodyAsText()
            )
        }
    }
    @OptIn(InternalAPI::class)
    @Test
    fun `access all heroes endpoints, assert correct information`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val expected = ApiResponse(
                success = true,
                message = "Ok",
                prevPage = null,
                nextPage = 2,
                heroes = heroRepository.page1
            )
            val actual = Json.decodeFromString<ApiResponse>(this.body())
            assertEquals(
                expected= expected,
                actual = actual
            )
        }
    }
}
















































