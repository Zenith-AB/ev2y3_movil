package com.duoc.materiald.ui.screens

// --- Importaciones Clave ---
import android.app.Application // <-- IMPORTACIÓN AÑADIDA
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // <-- IMPORTACIÓN AÑADIDA
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.materiald.viewmodel.RuletaViewModel // Importamos el ViewModel
import com.duoc.materiald.viewmodel.RuletaUiState // Importamos el Estado
import com.duoc.materiald.viewmodel.RuletaViewModelFactory // <-- IMPORTACIÓN AÑADIDA

@Composable
fun PantallaRuleta(
    navController: NavController,

    // --- CAMBIO HECHO AQUÍ ---
    // Ya no es = viewModel()
    // Ahora usa la Fábrica para poder inyectar el Repositorio de DataStore.
    viewModel: RuletaViewModel = viewModel(
        factory = RuletaViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
    // --- FIN DEL CAMBIO ---
) {
    // --- CONEXIÓN CON EL VIEWMODEL ---
    // Observamos el 'uiState' del ViewModel.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Llamamos al Composable que dibuja la UI,
    // pasándole el estado actual y las funciones del ViewModel.
    PantallaRuletaContent(
        uiState = uiState,
        onTextoOpcionChange = { viewModel.onTextoOpcionChange(it) },
        onAgregarOpcion = { viewModel.onAgregarOpcion() },
        onGirarRuleta = { viewModel.onGirarRuleta() },
        onBorrarOpcion = { viewModel.onBorrarOpcion(it) }
    )
}


@Composable
fun PantallaRuletaContent(
    uiState: RuletaUiState,
    onTextoOpcionChange: (String) -> Unit,
    onAgregarOpcion: () -> Unit,
    onGirarRuleta: () -> Unit,
    onBorrarOpcion: (String) -> Unit
) {
    // Usamos el 'uiState' para dibujar la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. Sección para agregar opciones
        Text("Añade Opciones a la Ruleta", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.textoOpcion, // El texto viene del ViewModel
            onValueChange = onTextoOpcionChange, // Avisamos al ViewModel del cambio
            label = { Text("Nueva opción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAgregarOpcion, // Avisamos al ViewModel del clic
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Opción")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Sección de la Ruleta y Resultado
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.estaGirando) {
                // Si está girando, mostramos un spinner
                CircularProgressIndicator(modifier = Modifier.size(100.dp))
            } else {
                // Si no, mostramos el resultado
                Text(
                    text = uiState.resultado.ifEmpty { "Pulsa 'Girar'" },
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para girar
        Button(
            onClick = onGirarRuleta,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.estaGirando // Deshabilitado mientras gira
        ) {
            Text("Girar Ruleta")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Lista de opciones actuales
        Text("Opciones Actuales (${uiState.opciones.size})", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Usamos una LazyColumn para mostrar la lista (eficiente)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(uiState.opciones) { opcion ->
                OpcionItem(
                    textoOpcion = opcion,
                    onBorrarClick = { onBorrarOpcion(opcion) } // Avisamos para borrar
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}



@Composable
fun OpcionItem(
    textoOpcion: String,
    onBorrarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(textoOpcion, modifier = Modifier.weight(1f))
            IconButton(onClick = onBorrarClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Borrar opción")
            }
        }
    }
}