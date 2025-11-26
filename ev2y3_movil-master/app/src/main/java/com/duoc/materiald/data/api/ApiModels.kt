package com.duoc.materiald.data.api

import com.google.gson.annotations.SerializedName

// Modelos para Opciones
data class OpcionResponse(
    val id: Int,
    val texto: String
)

data class OpcionRequest(
    val texto: String
)

// Modelos para Resultados
data class ResultadoResponse(
    val id: Int,
    val resultado: String,
    val timestamp: String? = null
)

data class ResultadoRequest(
    val resultado: String
)

// Respuesta genérica para operaciones de eliminación
data class DeleteResponse(
    val message: String,
    val changes: Int
)
