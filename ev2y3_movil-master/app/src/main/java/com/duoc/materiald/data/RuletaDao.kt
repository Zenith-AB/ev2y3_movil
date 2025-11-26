package com.duoc.materiald.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RuletaDao {

    // -------- OPCIONES ----------
    @Query("SELECT * FROM OpcionItem")
    fun getOpciones(): Flow<List<OpcionItem>>

    @Insert
    suspend fun insertOpcion(item: OpcionItem)

    @Delete
    suspend fun deleteOpcion(item: OpcionItem)

    // -------- HISTORIAL / RESULTADOS ----------
    @Query("SELECT * FROM ResultadoItem ORDER BY timestamp DESC")
    fun getResultados(): Flow<List<ResultadoItem>>

    @Insert
    suspend fun insertResultado(item: ResultadoItem)

    @Query("DELETE FROM ResultadoItem")
    suspend fun clearResultados()
}
