package com.duoc.materiald

import android.app.Application
import com.duoc.materiald.data.OpcionesRepository


class RuletaApplication : Application() {
    val opcionesRepository: OpcionesRepository by lazy {
        OpcionesRepository(this)
    }
}