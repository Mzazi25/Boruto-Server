package com.example

import com.example.models.ApiResponse
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*
import com.example.repository.HeroRepository
import com.example.repository.NEXT_PAGE_KEY
import com.example.repository.PREVIOUS_PAGE_KEY
import io.ktor.client.call.*
import io.ktor.server.application.*
import io.ktor.util.*
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

    @Test
    fun `assess all heroes endpoints, query all pages, assert correct information`()=
        testApplication {
        application {
           configureRouting()
        }
        val pages = 1..5
        val heroes = listOf(
            heroRepository.page1,
            heroRepository.page2,
            heroRepository.page3,
            heroRepository.page4,
            heroRepository.page5,
        )
        pages.forEach { page->
            client.get("/boruto/heroes?page=$page").apply{
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = status
                )
                val expected = ApiResponse(
                    success = true,
                    message = "Ok",
                    prevPage = calculatePage(page = page)["prevPage"],
                    nextPage = calculatePage(page = page)["nextPage"] ,
                    heroes = heroes[page-1]
                )
                val actual = Json.decodeFromString<ApiResponse>(this.body())

                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access all heroes endpoints, query invalid page, assert error`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=invalid").apply {
            assertEquals(
                expected = HttpStatusCode.BadRequest,
                actual = status
            )
            val expected = ApiResponse(
                success = false,
                message = "Only Numbers Allowed",

                )
            val actual = Json.decodeFromString<ApiResponse>(this.body())
            assertEquals(
                expected= expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access search heroes endpoints, query name, assert single hero result`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes/search?name=sas").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val actual = Json.decodeFromString<ApiResponse>(this.body()).heroes.size
            assertEquals(
                expected= 1,
                actual = actual
            )
        }
    }
    @Test
    fun `access all heroes endpoints, query non existing page number assert error`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=9").apply {
            assertEquals(
                expected = HttpStatusCode.NotFound,
                actual = status
            )
            val expected = ApiResponse(
                success = false,
                message = "Page not found",

            )
            val actual = Json.decodeFromString<ApiResponse>(this.body())
            assertEquals(
                expected= expected,
                actual = actual
            )
        }
    }
}

private fun calculatePage(page: Int):Map<String, Int?>{
    var nextPage: Int? = page
    var prevPage: Int? =page
    if (page in 1..4){
        nextPage = nextPage?.plus(1)
    }
    if (page in 2..5){
        prevPage = prevPage?.minus(1)
    }
    if (page == 5){
        nextPage = null
    }
    if (page == 1){
        prevPage =null
    }
    return mapOf(PREVIOUS_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
}














































