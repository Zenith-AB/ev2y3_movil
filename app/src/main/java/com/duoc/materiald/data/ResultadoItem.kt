package com.duoc.materiald.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_resultados")
data class ResultadoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreResultado: String,
    val fecha: Long = System.currentTimeMillis() // Guarda la fecha y hora
)