package com.duoc.materiald.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.duoc.materiald.data.AppDatabase
import com.duoc.materiald.data.RuletaRepository
import com.duoc.materiald.data.ThemePreferences
import com.duoc.materiald.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private const val RULETA_CHANNEL_ID = "ruletapp_channel"
private const val RULETA_NOTIFICATION_ID = 1001

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRuleta(navController: NavController) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { RuletaRepository(db.ruletaDao()) }
    val opcionesRoom by repository.getOpcionesLocal().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val themePreferences = remember { ThemePreferences(context) }
    val currentTheme by themePreferences.themeFlow.collectAsState(initial = "Clásico")

    LaunchedEffect(Unit) {
        createRuletaChannel(context)
        repository.syncOpciones()
        repository.syncResultados()
    }

    PantallaRuletaContent(
        opciones = opcionesRoom.map { it.texto },
        currentTheme = currentTheme,
        onNavigateToGestion = { navController.navigate("gestion") },
        onNavigateToHistorial = { navController.navigate("historial") },
        onNavigateToTemas = { navController.navigate("temas") },
        onTerminarGiro = { resultado ->
            scope.launch {
                repository.insertResultado(resultado, System.currentTimeMillis())
            }
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showRuletaNotification(context, resultado)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaRuletaContent(
    opciones: List<String>,
    currentTheme: String,
    onNavigateToGestion: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToTemas: () -> Unit,
    onTerminarGiro: (String) -> Unit
) {
    val rotation = remember { Animatable(0f) }
    var lastResultadoMostrado by remember { mutableStateOf<String?>(null) }
    var spinId by remember { mutableStateOf(0) }
    var isSpinning by remember { mutableStateOf(false) }
    var motivationalQuote by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(spinId) {
        if (spinId == 0) return@LaunchedEffect

        val opcionesParaMostrar =
            if (opciones.isEmpty()) (1..12).map { it.toString() } else opciones
        if (opcionesParaMostrar.isEmpty()) return@LaunchedEffect

        isSpinning = true
        showResult = false // Ocultar resultado anterior mientras gira
        val ganador = opcionesParaMostrar.random()

        val index = opcionesParaMostrar.indexOf(ganador).takeIf { it >= 0 } ?: 0
        val sweep = 360f / opcionesParaMostrar.size
        val desired = 90f - index * sweep - sweep / 2f

        val current = rotation.value
        val currentNorm = ((current % 360f) + 360f) % 360f
        var delta = desired - currentNorm
        delta = ((delta % 360f) + 360f) % 360f
        val target = current + 360f * 5 + delta

        rotation.animateTo(
            targetValue = target,
            animationSpec = tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing
            )
        )

        lastResultadoMostrado = ganador
        isSpinning = false
        showResult = true // Mostrar resultado después del giro
        
        // Obtener frase motivadora (API externa - ZenQuotes)
        try {
            android.util.Log.d("PantallaRuleta", "Intentando obtener frase motivadora...")
            val quotes = RetrofitClient.quoteService.getRandomQuote()
            if (quotes.isNotEmpty()) {
                val quote = quotes[0]
                motivationalQuote = "\"${quote.q}\" - ${quote.a}"
                android.util.Log.d("PantallaRuleta", "Frase obtenida: $motivationalQuote")
            } else {
                motivationalQuote = null
            }
        } catch (e: Exception) {
            android.util.Log.e("PantallaRuleta", "Error al obtener frase: ${e.message}", e)
            motivationalQuote = "Error al cargar frase motivadora: ${e.message}"
        }
        
        onTerminarGiro(ganador)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "RuletAPP",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToTemas) {
                        Icon(Icons.Default.Settings, contentDescription = "Temas")
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Ruleta
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val opcionesParaMostrar =
                    if (opciones.isEmpty()) (1..12).map { it.toString() } else opciones

                RuletaGrafica(
                    opciones = opcionesParaMostrar,
                    seleccion = lastResultadoMostrado,
                    tema = currentTheme,
                    modifier = Modifier.graphicsLayer(
                        rotationZ = rotation.value % 360f
                    )
                )

                IndicadorRuleta(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 4.dp)
                )

                // Botón central
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(4.dp, CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clickable(enabled = !isSpinning) { spinId += 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSpinning) "..." else "GIRAR",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Resultado
            AnimatedVisibility(
                visible = lastResultadoMostrado != null && !isSpinning && showResult,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResult = false },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Resultado",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            lastResultadoMostrado ?: "",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        // Frase motivadora (API externa)
                        if (motivationalQuote != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                motivationalQuote!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Opciones (${opciones.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de opciones
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(opciones) { opcion ->
                    val esGanadora = opcion == lastResultadoMostrado && !isSpinning
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esGanadora)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (esGanadora) 4.dp else 2.dp
                        )
                    ) {
                        Text(
                            text = opcion,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (esGanadora) FontWeight.Bold else FontWeight.Normal,
                            color = if (esGanadora)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToGestion,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gestionar")
                }
                Button(
                    onClick = onNavigateToHistorial,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Historial")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuletaGrafica(
    opciones: List<String>,
    seleccion: String?,
    tema: String,
    modifier: Modifier = Modifier
) {
    val colores = when (tema) {
        "Oscuro" -> listOf(
            Color(0xFF90CAF9), Color(0xFF80CBC4), Color(0xFFCE93D8),
            Color(0xFFA5D6A7), Color(0xFFFFCC80), Color(0xFFEF9A9A)
        )
        "Pastel" -> listOf(
            Color(0xFFFF9AA2), Color(0xFFFFB7B2), Color(0xFFFFC3A0),
            Color(0xFFFFDAC1), Color(0xFFE2F0CB), Color(0xFFB5EAD7)
        )
        else -> listOf( // Clásico
            Color(0xFFB0C4DE), Color(0xFFD8BFD8), Color(0xFFF5DEB3),
            Color(0xFF98FB98), Color(0xFFFFCCCB), Color(0xFFE6E6FA)
        )
    }

    Canvas(modifier = modifier.size(280.dp)) {
        val total = opciones.size.coerceAtLeast(1)
        val sweep = 360f / total
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        opciones.forEachIndexed { index, opcion ->
            val startAngle = -90f + (index * sweep)
            val baseColor = colores[index % colores.size]
            val fillColor = if (opcion == seleccion && seleccion != null)
                baseColor.copy(alpha = 1f)
            else
                baseColor.copy(alpha = 0.85f)

            drawArc(
                color = fillColor,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // Texto mejorado
            val middleAngle = startAngle + sweep / 2f
            val textRadius = radius * 0.65f
            val rad = Math.toRadians(middleAngle.toDouble())
            val textX = center.x + (textRadius * cos(rad)).toFloat()
            val textY = center.y + (textRadius * sin(rad)).toFloat()

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = 32f
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        android.graphics.Typeface.BOLD
                    )
                }
                canvas.nativeCanvas.drawText(
                    opcion,
                    textX,
                    textY + 12f,
                    paint
                )
            }
        }

        // Círculo central con sombra
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = radius * 0.32f,
            center = center
        )
        drawCircle(
            color = Color.White,
            radius = radius * 0.28f,
            center = center
        )
    }
}

@Composable
private fun IndicadorRuleta(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(32.dp)) {
        val path = Path().apply {
            moveTo(0f, size.height / 2f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(
            path = path,
            color = Color(0xFFE53935)
        )
    }
}

private fun createRuletaChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            RULETA_CHANNEL_ID,
            "Resultados de la ruleta",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifica cuando hay un nuevo resultado en RuletAPP"
        }
        val nm = context.getSystemService(NotificationManager::class.java)
        nm?.createNotificationChannel(channel)
    }
}

private fun showRuletaNotification(context: Context, resultado: String) {
    val builder = NotificationCompat.Builder(context, RULETA_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("RuletAPP")
        .setContentText("Salió: $resultado")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(RULETA_NOTIFICATION_ID, builder.build())
    }
}
