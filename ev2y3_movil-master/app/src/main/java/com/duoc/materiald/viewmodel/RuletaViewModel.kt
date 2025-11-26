package com.duoc.materiald.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.materiald.data.OpcionesRepository //
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RuletaUiState(
    val textoOpcion: String = "",
    val opciones: List<String> = emptyList(), // <-- 2. CAMBIADO: Inicia vacío
    val resultado: String = "",
    val estaGirando: Boolean = false
)


// --- 3. CAMBIADO: Ahora pide el Repositorio en el constructor ---
class RuletaViewModel(
    private val opcionesRepository: OpcionesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuletaUiState())

    val uiState: StateFlow<RuletaUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            opcionesRepository.opciones.collect { opcionesDeDataStore ->

                _uiState.update { currentState ->
                    currentState.copy(opciones = opcionesDeDataStore)
                }
            }
        }
    }

    // --- MANEJADORES DE EVENTOS ---


    fun onTextoOpcionChange(nuevoTexto: String) {
        _uiState.update { currentState ->
            currentState.copy(textoOpcion = nuevoTexto)
        }
    }


    fun onAgregarOpcion() {
        if (_uiState.value.textoOpcion.isBlank()) {
            return
        }

        // --- 5. CAMBIADO: Ahora guarda en el Repositorio ---
        val nuevasOpciones = _uiState.value.opciones + _uiState.value.textoOpcion

        // Lanzamos una corrutina para la operación de guardado (E/S)
        viewModelScope.launch {
            opcionesRepository.guardarOpciones(nuevasOpciones)
        }

        // Solo actualizamos el campo de texto en la UI.
        // La lista se actualizará "sola" gracias al 'collect' del init {}
        _uiState.update { currentState ->
            currentState.copy(
                textoOpcion = ""
            )
        }
    }

    fun onGirarRuleta() {
        val opciones = _uiState.value.opciones

        if (opciones.isEmpty()) {
            _uiState.update { it.copy(resultado = "¡Añade opciones primero!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(estaGirando = true, resultado = "") }
            delay(2000)
            val resultadoFinal = opciones.random()
            _uiState.update {
                it.copy(
                    estaGirando = false,
                    resultado = "¡El ganador es: $resultadoFinal!"
                )
            }
        }
    }
    fun onBorrarOpcion(opcionABorrar: String) {

        // --- 6. CAMBIADO: Ahora guarda en el Repositorio ---
        val nuevasOpciones = _uiState.value.opciones.filter { it != opcionABorrar }

        // Lanzamos una corrutina para la operación de guardado (E/S)
        viewModelScope.launch {
            opcionesRepository.guardarOpciones(nuevasOpciones)
        }
        // La UI se actualizará "sola" gracias al 'collect' del init {}
    }
}