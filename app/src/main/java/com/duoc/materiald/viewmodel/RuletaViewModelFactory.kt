package com.duoc.materiald.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duoc.materiald.RuletaApplication

/**
 * Fábrica para crear nuestro RuletaViewModel.
 * Sabe cómo obtener el Repositorio desde nuestra RuletaApplication.
 */
class RuletaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprobamos si la clase que nos piden es RuletaViewModel
        if (modelClass.isAssignableFrom(RuletaViewModel::class.java)) {

            // Obtenemos el repositorio desde nuestra Application
            val repository = (application as RuletaApplication).opcionesRepository

            // Creamos y devolvemos la instancia del ViewModel
            @Suppress("UNCHECKED_CAST")
            return RuletaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}