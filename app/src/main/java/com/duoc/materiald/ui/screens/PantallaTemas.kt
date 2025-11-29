package com.duoc.materiald.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duoc.materiald.data.ThemePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTemas(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePreferences = remember { ThemePreferences(context) }
    val currentTheme by themePreferences.themeFlow.collectAsState(initial = "Clásico")

    val temas = listOf(
        Tema("Clásico", listOf(Color(0xFFB0C4DE), Color(0xFFD8BFD8), Color(0xFFF5DEB3)), "El estilo original con colores suaves"),
        Tema("Oscuro", listOf(Color(0xFF90CAF9), Color(0xFF80CBC4), Color(0xFFCE93D8)), "Modo oscuro elegante y moderno"),
        Tema("Pastel", listOf(Color(0xFFFF9AA2), Color(0xFFFFB7B2), Color(0xFFB5EAD7)), "Tonos suaves y relajantes")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Elegir Tema",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Personaliza la apariencia de tu app",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(temas) { tema ->
                    val isSelected = tema.nombre == currentTheme
                    ItemTema(
                        tema = tema,
                        isSelected = isSelected,
                        onClick = {
                            scope.launch {
                                themePreferences.saveTheme(tema.nombre)
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemTema(tema: Tema, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color preview
            Row(
                modifier = Modifier.size(60.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                tema.colores.take(3).forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color, shape = MaterialTheme.shapes.small)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tema.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tema.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

data class Tema(val nombre: String, val colores: List<Color>, val descripcion: String)
