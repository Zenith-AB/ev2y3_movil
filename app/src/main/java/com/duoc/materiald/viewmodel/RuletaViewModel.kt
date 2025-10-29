package com.duoc.materiald.viewmodel

// Importaciones necesarias
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RuletaUiState(
    val textoOpcion: String = "", // El texto actual en el TextField
    val opciones: List<String> = listOf("Opción 1", "Opción 2"), // La lista de opciones
    val resultado: String = "",     // El resultado después de girar
    val estaGirando: Boolean = false // Para saber si mostrar una animación o no
)


class RuletaViewModel : ViewModel() {

    // _uiState es PRIVADO y Mutable. Solo el ViewModel puede cambiarlo.
    private val _uiState = MutableStateFlow(RuletaUiState())

    // uiState es PÚBLICO e Inmutable. La UI solo puede LEER de él.
    val uiState: StateFlow<RuletaUiState> = _uiState.asStateFlow()


    // --- MANEJADORES DE EVENTOS ---
    fun onTextoOpcionChange(nuevoTexto: String) {
        _uiState.update { currentState ->
            currentState.copy(textoOpcion = nuevoTexto)
        }
    }

    fun onAgregarOpcion() {
        // Evitamos agregar opciones vacías
        if (_uiState.value.textoOpcion.isBlank()) {
            return
        }

        _uiState.update { currentState ->
            // Creamos una nueva lista añadiendo la nueva opción
            val nuevasOpciones = currentState.opciones + currentState.textoOpcion
            currentState.copy(
                opciones = nuevasOpciones,
                textoOpcion = "" // Limpiamos el campo de texto
            )
        }
    }

    fun onGirarRuleta() {
        val opciones = _uiState.value.opciones

        // No girar si no hay opciones
        if (opciones.isEmpty()) {
            _uiState.update { it.copy(resultado = "¡Añade opciones primero!") }
            return
        }

        // Usamos viewModelScope para lanzar una corrutina (trabajo asíncrono)
        viewModelScope.launch {
            // 1. Poner la UI en modo "girando"
            _uiState.update { it.copy(estaGirando = true, resultado = "") }

            // 2. Simular el tiempo de giro (ej. 2 segundos)
            delay(2000) // Espera 2000 milisegundos

            // 3. Obtener el resultado
            val resultadoFinal = opciones.random()

            // 4. Actualizar la UI con el resultado y detener el giro
            _uiState.update {
                it.copy(
                    estaGirando = false,
                    resultado = "¡El ganador es: $resultadoFinal!"
                )
            }
        }
    }
    fun onBorrarOpcion(opcionABorrar: String) {
        _uiState.update { currentState ->
            val nuevasOpciones = currentState.opciones.filter { it != opcionABorrar }
            currentState.copy(opciones = nuevasOpciones)
        }
    }
}