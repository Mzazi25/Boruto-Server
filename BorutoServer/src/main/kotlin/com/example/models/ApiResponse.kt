package com.example.models

data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
    val prevPage: String? = null,
    val nextPage: String? = null,
    val heroes: List<Hero> = emptyList()
)
