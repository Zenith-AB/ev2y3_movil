package com.duoc.materiald.ui.screens
import android.app.Application
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.duoc.materiald.viewmodel.RuletaViewModel
import com.duoc.materiald.viewmodel.RuletaUiState
import com.duoc.materiald.viewmodel.RuletaViewModelFactory

/**
 * Pantalla principal que muestra la ruleta y permite añadir/girar opciones.
 */
@Composable
fun PantallaRuleta(
    navController: NavController, // Recibe el NavController para poder navegar
    viewModel: RuletaViewModel = viewModel(
        factory = RuletaViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Llama al Composable de contenido, pasándole el estado, los eventos del ViewModel
    // y las acciones de navegación conectadas al NavController.
    PantallaRuletaContent(
        uiState = uiState,
        onTextoOpcionChange = viewModel::onTextoOpcionChange, // Forma corta de pasar la función
        onAgregarOpcion = viewModel::onAgregarOpcion,
        onGirarRuleta = viewModel::onGirarRuleta,
        onBorrarOpcion = viewModel::onBorrarOpcion,
        // --- Acciones de Navegación (implementación de Acción 2) ---
        onNavigateToGestion = { navController.navigate("gestion") }, // Navega a la ruta "gestion"
        onNavigateToHistorial = { navController.navigate("historial") } // Navega a la ruta "historial"
    )
}

/**
 * Composable que dibuja la interfaz de la PantallaRuleta.
 * Recibe el estado y las funciones a las que llamar (eventos).
 */
@Composable
fun PantallaRuletaContent(
    uiState: RuletaUiState,
    onTextoOpcionChange: (String) -> Unit,
    onAgregarOpcion: () -> Unit,
    onGirarRuleta: () -> Unit,
    onBorrarOpcion: (String) -> Unit,
    // --- Parámetros para la navegación ---
    onNavigateToGestion: () -> Unit,
    onNavigateToHistorial: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. Sección para agregar opciones (sin cambios)
        Text("Añade Opciones a la Ruleta", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.textoOpcion,
            onValueChange = onTextoOpcionChange,
            label = { Text("Nueva opción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onAgregarOpcion, modifier = Modifier.fillMaxWidth()) {
            Text("Agregar Opción")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Sección de la Ruleta y Resultado (sin cambios)
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.estaGirando) {
                CircularProgressIndicator(modifier = Modifier.size(100.dp))
            } else {
                Text(
                    text = uiState.resultado.ifEmpty { "Pulsa 'Girar'" },
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onGirarRuleta,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.estaGirando
        ) {
            Text("Girar Ruleta")
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 3. Lista de opciones actuales (con .weight(1f) añadido en Acción 1)
        Text("Opciones Actuales (${uiState.opciones.size})", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // Hacemos que la lista ocupe el espacio sobrante
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(uiState.opciones) { opcion ->
                OpcionItem(
                    textoOpcion = opcion,
                    onBorrarClick = { onBorrarOpcion(opcion) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // --- 4. BOTONES DE NAVEGACIÓN (Añadidos en Acción 1, conectados en Acción 2) ---
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Los onClick AHORA llaman a las funciones recibidas como parámetro
            Button(onClick = onNavigateToGestion) {
                Text("Gestionar")
            }
            Button(onClick = onNavigateToHistorial) {
                Text("Historial")
            }
        }
        // --- FIN BOTONES DE NAVEGACIÓN ---
    } // Fin de la Column principal
}

/**
 * Composable para mostrar un item de la lista de opciones con botón de borrar.
 * (Sin cambios)
 */
@Composable
fun OpcionItem(
    textoOpcion: String,
    onBorrarClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(textoOpcion, modifier = Modifier.weight(1f))
            IconButton(onClick = onBorrarClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Borrar opción $textoOpcion")
            }
        }
    }
}