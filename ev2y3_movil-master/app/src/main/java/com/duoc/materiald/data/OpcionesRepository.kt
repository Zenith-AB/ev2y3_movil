package com.duoc.materiald.data

import com.duoc.materiald.data.api.OpcionRequest
import com.duoc.materiald.data.api.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repositorio que gestiona las opciones usando la API backend.
 * Mantiene la interfaz Flow para compatibilidad con el ViewModel existente.
 */
class OpcionesRepository {

    private val apiService = RetrofitInstance.apiService

    /**
     * Obtiene las opciones desde la API como un Flow.
     * Se actualiza cada vez que se llama a guardarOpciones.
     */
    val opciones: Flow<List<String>> = flow {
        while (true) {
            try {
                val opcionesFromApi = apiService.getOpciones()
                emit(opcionesFromApi.map { it.texto })
            } catch (e: Exception) {
                // En caso de error, emitir lista vacía
                emit(emptyList())
            }
            // Polling cada 2 segundos para mantener la UI actualizada
            delay(2000)
        }
    }

    /**
     * Guarda las opciones en la API.
     * Compara con las opciones actuales y realiza las operaciones necesarias.
     */
    suspend fun guardarOpciones(nuevasOpciones: List<String>) {
        try {
            // Obtener opciones actuales de la API
            val opcionesActuales = apiService.getOpciones()
            
            // Determinar qué opciones agregar
            val opcionesAAgregar = nuevasOpciones.filter { nueva ->
                opcionesActuales.none { it.texto == nueva }
            }
            
            // Determinar qué opciones eliminar
            val opcionesAEliminar = opcionesActuales.filter { actual ->
                actual.texto !in nuevasOpciones
            }
            
            // Agregar nuevas opciones
            opcionesAAgregar.forEach { texto ->
                apiService.createOpcion(OpcionRequest(texto))
            }
            
            // Eliminar opciones que ya no están
            opcionesAEliminar.forEach { opcion ->
                apiService.deleteOpcion(opcion.id)
            }
        } catch (e: Exception) {
            // Manejar errores silenciosamente por ahora
            e.printStackTrace()
        }
    }
}