package com.duoc.materiald.data.remote

import retrofit2.http.*

interface SupabaseService {
    
    @GET("rest/v1/opciones?select=*&order=created_at.desc")
    suspend fun getOpciones(
        @Header("apikey") apiKey: String
    ): List<OpcionRemote>
    
    @POST("rest/v1/opciones")
    @Headers("Prefer: return=representation")
    suspend fun insertOpcion(
        @Header("apikey") apiKey: String,
        @Body opcion: OpcionRemote
    ): List<OpcionRemote>
    
    @DELETE("rest/v1/opciones")
    suspend fun deleteOpcion(
        @Header("apikey") apiKey: String,
        @Query("id") id: String
    )
    
    @GET("rest/v1/resultados?select=*&order=timestamp.desc")
    suspend fun getResultados(
        @Header("apikey") apiKey: String
    ): List<ResultadoRemote>
    
    @POST("rest/v1/resultados")
    @Headers("Prefer: return=representation")
    suspend fun insertResultado(
        @Header("apikey") apiKey: String,
        @Body resultado: ResultadoRemote
    ): List<ResultadoRemote>
    
    @DELETE("rest/v1/resultados")
    suspend fun clearResultados(
        @Header("apikey") apiKey: String
    )
}
