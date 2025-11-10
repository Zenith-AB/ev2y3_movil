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
import com.duoc.materiald.data.ResultadoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.ruletaDao() }

    val historial by dao.getResultados().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("ruleta") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver a la ruleta"
                        )
                    }
                },
                actions = {
                    if (historial.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    dao.clearResultados()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Limpiar historial"
                            )
                        }
                    }
                }
            )
        }
    ) { inner ->
        if (historial.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no hay resultados")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(historial) { item: ResultadoItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = item.resultado,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "ts: ${item.timestamp}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
