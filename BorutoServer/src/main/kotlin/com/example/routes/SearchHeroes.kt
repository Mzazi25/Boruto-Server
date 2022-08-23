package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.searchHeroes(){
    get("/boruto/heroes/search") {
        val name = call.request.queryParameters["name"]
    }
}