package com.duoc.materiald.data.remote

import retrofit2.http.GET

interface QuoteService {
    @GET("api/random")
    suspend fun getRandomQuote(): List<ZenQuote>
}

data class ZenQuote(
    val q: String,  // quote text
    val a: String,  // author
    val h: String   // html formatted
)
