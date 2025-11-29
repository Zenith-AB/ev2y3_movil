package com.duoc.materiald.data.remote

data class OpcionRemote(
    val id: Long? = null,
    val texto: String,
    val created_at: String? = null
)

data class ResultadoRemote(
    val id: Long? = null,
    val resultado: String,
    val timestamp: Long,
    val created_at: String? = null
)

data class EstadisticasResponse(
    val total: Int,
    val masComun: MasComun?,
    val distribucion: Map<String, Int>
)

data class MasComun(
    val opcion: String,
    val veces: Int
)
