package com.duoc.materiald

import com.duoc.materiald.data.OpcionItem
import com.duoc.materiald.data.RuletaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RuletaRepositoryTest {
    
    private lateinit var fakeDao: FakeRuletaDao
    private lateinit var repository: RuletaRepository
    
    @Before
    fun setup() {
        fakeDao = FakeRuletaDao()
        repository = RuletaRepository(fakeDao)
    }
    
    @Test
    fun `insertar opcion actualiza lista local`() = runTest {
        // Given
        val texto = "Pizza"
        
        // When
        repository.insertOpcion(texto)
        
        // Then
        val opciones = repository.getOpcionesLocal().first()
        assertTrue(opciones.any { it.texto == texto })
    }
    
    @Test
    fun `eliminar opcion remueve de lista local`() = runTest {
        // Given
        val opcion = OpcionItem(id = 1, texto = "Hamburguesa")
        repository.insertOpcion(opcion.texto)
        
        // When
        val opciones = repository.getOpcionesLocal().first()
        repository.deleteOpcion(opciones.first())
        
        // Then
        val opcionesActualizadas = repository.getOpcionesLocal().first()
        assertTrue(opcionesActualizadas.isEmpty())
    }
    
    @Test
    fun `insertar resultado guarda timestamp`() = runTest {
        // Given
        val resultado = "Pizza"
        val timestamp = System.currentTimeMillis()
        
        // When
        repository.insertResultado(resultado, timestamp)
        
        // Then
        val resultados = repository.getResultadosLocal().first()
        assertEquals(1, resultados.size)
        assertEquals(resultado, resultados.first().resultado)
        assertEquals(timestamp, resultados.first().timestamp)
    }
    
    @Test
    fun `clear resultados limpia lista`() = runTest {
        // Given
        repository.insertResultado("Pizza", System.currentTimeMillis())
        repository.insertResultado("Pasta", System.currentTimeMillis())
        
        // When
        repository.clearResultados()
        
        // Then
        val resultados = repository.getResultadosLocal().first()
        assertTrue(resultados.isEmpty())
    }
}
