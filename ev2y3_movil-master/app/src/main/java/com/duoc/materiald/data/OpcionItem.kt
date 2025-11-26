package com.duoc.materiald.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OpcionItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val texto: String
)
