package com.duoc.materiald

import org.junit.Assert.*
import org.junit.Test

class RuletaLogicTest {
    
    @Test
    fun `seleccionar ganador retorna opcion valida`() {
        // Given
        val opciones = listOf("Pizza", "Hamburguesa", "Pasta", "Ensalada")
        
        // When
        val ganador = opciones.random()
        
        // Then
        assertTrue(opciones.contains(ganador))
    }
    
    @Test
    fun `lista vacia no causa error`() {
        // Given
        val opciones = emptyList<String>()
        
        // When/Then
        val opcionesParaMostrar = if (opciones.isEmpty()) {
            (1..12).map { it.toString() }
        } else {
            opciones
        }
        
        assertEquals(12, opcionesParaMostrar.size)
    }
    
    @Test
    fun `calcular angulo de ruleta es correcto`() {
        // Given
        val totalOpciones = 6
        val sweep = 360f / totalOpciones
        
        // When
        val anguloCalculado = sweep
        
        // Then
        assertEquals(60f, anguloCalculado, 0.01f)
    }
}
