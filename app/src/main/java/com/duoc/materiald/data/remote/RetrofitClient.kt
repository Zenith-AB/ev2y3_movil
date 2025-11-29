package com.duoc.materiald.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val SUPABASE_URL = "https://zetlbhufsklsogjzritx.supabase.co/"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpldGxiaHVmc2tsc29nanpyaXR4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzODQ4NjEsImV4cCI6MjA3OTk2MDg2MX0.iWJT_sPv18x-Zip0yHN0WtqycEWocfQCgELLcSrvPv8"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val supabaseService: SupabaseService = retrofit.create(SupabaseService::class.java)
    
    // Quote API (externa) - ZenQuotes
    private val quoteRetrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val quoteService: QuoteService = quoteRetrofit.create(QuoteService::class.java)
}
