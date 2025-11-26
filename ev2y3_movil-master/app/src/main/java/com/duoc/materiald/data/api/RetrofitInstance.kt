package com.duoc.materiald.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    
    // URL base - usa 10.0.2.2 para emulador Android (apunta a localhost de la máquina host)
    // Para Railway, cambiar a la URL pública: "https://tu-proyecto.up.railway.app/"
    private const val BASE_URL = "http://10.0.2.2:3000/"
    
    // Cliente HTTP con logging para debugging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Instancia de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API service
    val apiService: RuletaApiService by lazy {
        retrofit.create(RuletaApiService::class.java)
    }
}
