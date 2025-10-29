package com.duoc.materiald.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class OpcionesRepository(private val context: Context) {

    private val OPCIONES_KEY = stringSetPreferencesKey("lista_opciones")


    val opciones: Flow<List<String>> = context.dataStore.data
        .map { preferences ->

            val set = preferences[OPCIONES_KEY] ?: emptySet()

            set.toList().sorted()
        }


    suspend fun guardarOpciones(opciones: List<String>) {
        context.dataStore.edit { settings ->

            settings[OPCIONES_KEY] = opciones.toSet()
        }
    }
}