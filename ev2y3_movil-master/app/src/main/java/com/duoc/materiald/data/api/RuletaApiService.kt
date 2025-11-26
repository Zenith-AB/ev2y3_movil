package com.duoc.materiald.data.api

import retrofit2.http.*

interface RuletaApiService {
    
    // Endpoints para Opciones
    @GET("api/opciones")
    suspend fun getOpciones(): List<OpcionResponse>
    
    @POST("api/opciones")
    suspend fun createOpcion(@Body request: OpcionRequest): OpcionResponse
    
    @DELETE("api/opciones/{id}")
    suspend fun deleteOpcion(@Path("id") id: Int): DeleteResponse
    
    // Endpoints para Resultados
    @GET("api/resultados")
    suspend fun getResultados(): List<ResultadoResponse>
    
    @POST("api/resultados")
    suspend fun createResultado(@Body request: ResultadoRequest): ResultadoResponse
    
    @DELETE("api/resultados/{id}")
    suspend fun deleteResultado(@Path("id") id: Int): DeleteResponse
}
