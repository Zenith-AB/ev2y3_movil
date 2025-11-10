package com.duoc.materiald.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ResultadoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val resultado: String,
    val timestamp: Long = System.currentTimeMillis()
)
