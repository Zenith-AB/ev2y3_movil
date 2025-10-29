package com.duoc.materiald.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RuletaDao {
    // --- Funciones para Opciones ---
    @Query("SELECT * FROM opciones_ruleta")
    fun getAllOpciones(): Flow<List<OpcionItem>> // Flow es para que se actualice solo (MVVM)

    @Insert
    suspend fun insertOpcion(opcion: OpcionItem)

    @Delete
    suspend fun deleteOpcion(opcion: OpcionItem)

    // --- Funciones para Resultados (Historial) ---
    @Query("SELECT * FROM historial_resultados ORDER BY fecha DESC")
    fun getAllResultados(): Flow<List<ResultadoItem>>

    @Insert
    suspend fun insertResultado(resultado: ResultadoItem)
}