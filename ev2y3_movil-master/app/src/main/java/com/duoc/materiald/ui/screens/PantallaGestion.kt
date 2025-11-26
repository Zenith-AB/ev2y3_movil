package com.duoc.materiald.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duoc.materiald.data.AppDatabase
import com.duoc.materiald.data.OpcionItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestion(
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.ruletaDao() }

    // escuchamos las opciones de la BD
    val opciones by dao.getOpciones().collectAsState(initial = emptyList())

    var nuevaOpcion by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de opciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("ruleta") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver a la ruleta"
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = nuevaOpcion,
                onValueChange = { nuevaOpcion = it },
                label = { Text("Nueva opción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val texto = nuevaOpcion.trim()
                    if (texto.isNotEmpty()) {
                        scope.launch {
                            dao.insertOpcion(OpcionItem(texto = texto))
                        }
                        nuevaOpcion = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar opción")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Opciones actuales (${opciones.size})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(opciones) { opcion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = opcion.texto)
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        dao.deleteOpcion(opcion)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar opción"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
