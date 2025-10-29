package com.duoc.materiald.data // Asegúrate que el paquete sea 'data'

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "opciones_ruleta")
data class OpcionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)