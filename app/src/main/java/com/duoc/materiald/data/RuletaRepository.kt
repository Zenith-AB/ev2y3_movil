package com.duoc.materiald.data

import android.util.Log
import com.duoc.materiald.data.remote.OpcionRemote
import com.duoc.materiald.data.remote.ResultadoRemote
import com.duoc.materiald.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RuletaRepository(private val dao: RuletaDao) {
    
    private val api = RetrofitClient.supabaseService
    private val apiKey = RetrofitClient.SUPABASE_ANON_KEY
    
    // Opciones
    fun getOpcionesLocal(): Flow<List<OpcionItem>> = dao.getOpciones()
    
    suspend fun syncOpciones() {
        try {
            val remotas = api.getOpciones(apiKey)
            // Limpiar locales y agregar remotas
            dao.clearOpciones()
            remotas.forEach { remota ->
                dao.insertOpcion(OpcionItem(id = (remota.id ?: 0).toInt(), texto = remota.texto))
            }
            Log.d("RuletaRepo", "Opciones sincronizadas: ${remotas.size}")
        } catch (e: Exception) {
            Log.e("RuletaRepo", "Error sincronizando opciones", e)
        }
    }
    
    suspend fun insertOpcion(texto: String) {
        try {
            // Insertar en Supabase
            val remota = api.insertOpcion(apiKey, OpcionRemote(texto = texto))
            // Insertar en Room
            if (remota.isNotEmpty()) {
                dao.insertOpcion(OpcionItem(id = (remota[0].id ?: 0).toInt(), texto = remota[0].texto))
            }
        } catch (e: Exception) {
            Log.e("RuletaRepo", "Error insertando opción", e)
            // Fallback: insertar solo en Room
            dao.insertOpcion(OpcionItem(texto = texto))
        }
    }
    
    suspend fun deleteOpcion(opcion: OpcionItem) {
        try {
            // Eliminar de Supabase
            api.deleteOpcion(apiKey, "eq.${opcion.id}")
            // Eliminar de Room
            dao.deleteOpcion(opcion)
        } catch (e: Exception) {
            Log.e("RuletaRepo", "Error eliminando opción", e)
            // Fallback: eliminar solo de Room
            dao.deleteOpcion(opcion)
        }
    }
    
    // Resultados
    fun getResultadosLocal(): Flow<List<ResultadoItem>> = dao.getResultados()
    
    suspend fun syncResultados() {
        try {
            val remotos = api.getResultados(apiKey)
            // Limpiar locales y agregar remotos
            dao.clearResultados()
            remotos.forEach { remoto ->
                dao.insertResultado(
                    ResultadoItem(
                        id = (remoto.id ?: 0).toInt(),
                        resultado = remoto.resultado,
                        timestamp = remoto.timestamp
                    )
                )
            }
            Log.d("RuletaRepo", "Resultados sincronizados: ${remotos.size}")
        } catch (e: Exception) {
            Log.e("RuletaRepo", "Error sincronizando resultados", e)
        }
    }
    
    suspend fun insertResultado(resultado: String, timestamp: Long) {
        try {
            // Insertar en Supabase
            val remoto = api.insertResultado(
                apiKey,
                ResultadoRemote(resultado = resultado, timestamp = timestamp)
            )
            // Insertar en Room
            if (remoto.isNotEmpty()) {
                dao.insertResultado(
                    ResultadoItem(
                        id = (remoto[0].id ?: 0).toInt(),
                        resultado = remoto[0].resultado,
                        timestamp = remoto[0].timestamp
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("RuletaRepo", "Error insertando resultado", e)
            // Fallback: insertar solo en Room
            dao.insertResultado(ResultadoItem(resultado = resultado, timestamp = timestamp))
        }
    }
    
    suspend fun clearResultados() {
        try {
            // Limpiar en Supabase
            api.clearResultados(apiKey)
            // Limpiar en Room
            dao.clearResultados()
        } catch (e: Exception) {
            Log.e("RuletaRepo", "Error limpiando resultados", e)
            // Fallback: limpiar solo Room
            dao.clearResultados()
        }
    }
}
