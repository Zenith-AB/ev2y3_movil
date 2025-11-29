package com.duoc.materiald

import com.duoc.materiald.data.OpcionItem
import com.duoc.materiald.data.ResultadoItem
import com.duoc.materiald.data.RuletaDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRuletaDao : RuletaDao {
    private val opciones = mutableListOf<OpcionItem>()
    private val resultados = mutableListOf<ResultadoItem>()
    
    override fun getOpciones(): Flow<List<OpcionItem>> = flowOf(opciones)
    
    override suspend fun insertOpcion(item: OpcionItem) {
        opciones.add(item.copy(id = opciones.size + 1))
    }
    
    override suspend fun deleteOpcion(item: OpcionItem) {
        opciones.remove(item)
    }
    
    override suspend fun clearOpciones() {
        opciones.clear()
    }
    
    override fun getResultados(): Flow<List<ResultadoItem>> = flowOf(resultados)
    
    override suspend fun insertResultado(item: ResultadoItem) {
        resultados.add(item.copy(id = resultados.size + 1))
    }
    
    override suspend fun clearResultados() {
        resultados.clear()
    }
}
