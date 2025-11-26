package com.duoc.materiald.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.duoc.materiald.data.AppDatabase
import com.duoc.materiald.data.ResultadoItem
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ----------------------------------------
// constantes para noti
// ----------------------------------------
private const val RULETA_CHANNEL_ID = "ruletapp_channel"
private const val RULETA_NOTIFICATION_ID = 1001

@Composable
fun PantallaRuleta(
    navController: NavController
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // BD
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.ruletaDao() }
    val opcionesRoom by dao.getOpciones().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // crear canal una vez
    LaunchedEffect(Unit) {
        createRuletaChannel(context)
    }

    PantallaRuletaContent(
        opciones = opcionesRoom.map { it.texto },
        onNavigateToGestion = { navController.navigate("gestion") },
        onNavigateToHistorial = { navController.navigate("historial") },
        onTerminarGiro = { resultado ->
            // 1. guardar en Room
            scope.launch {
                dao.insertResultado(
                    ResultadoItem(resultado = resultado)
                )
            }
            // 2. haptic (no necesita permisos)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            // 3. notificación
            showRuletaNotification(context, resultado)
        }
    )
}

@Composable
private fun PantallaRuletaContent(
    opciones: List<String>,
    onNavigateToGestion: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onTerminarGiro: (String) -> Unit
) {
    val rotation = remember { Animatable(0f) }
    var lastResultadoMostrado by remember { mutableStateOf<String?>(null) }
    var spinId by remember { mutableStateOf(0) }

    LaunchedEffect(spinId) {
        if (spinId == 0) return@LaunchedEffect

        val opcionesParaMostrar =
            if (opciones.isEmpty()) (1..12).map { it.toString() } else opciones
        if (opcionesParaMostrar.isEmpty()) return@LaunchedEffect

        val ganador = opcionesParaMostrar.random()

        // calcular ángulo
        val index = opcionesParaMostrar.indexOf(ganador).takeIf { it >= 0 } ?: 0
        val sweep = 360f / opcionesParaMostrar.size
        val desired = 90f - index * sweep - sweep / 2f

        val current = rotation.value
        val currentNorm = ((current % 360f) + 360f) % 360f
        var delta = desired - currentNorm
        delta = ((delta % 360f) + 360f) % 360f
        val target = current + 360f * 4 + delta

        // animar
        rotation.animateTo(
            targetValue = target,
            animationSpec = tween(
                durationMillis = 2300,
                easing = FastOutSlowInEasing
            )
        )

        // recién cuando termina: prendemos la opción
        lastResultadoMostrado = ganador

        // y avisamos para guardar + haptic + noti
        onTerminarGiro(ganador)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // título
        Text(
            text = "RuletAPP",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ruleta
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            val opcionesParaMostrar =
                if (opciones.isEmpty()) (1..12).map { it.toString() } else opciones

            RuletaGrafica(
                opciones = opcionesParaMostrar,
                seleccion = lastResultadoMostrado,
                modifier = Modifier.graphicsLayer(
                    rotationZ = rotation.value % 360f
                )
            )

            IndicadorRuleta(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 2.dp)
            )

            // botón central
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable { spinId += 1 },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Opciones actuales (${opciones.size})",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // lista
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(opciones) { opcion ->
                val esGanadora = opcion == lastResultadoMostrado
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (esGanadora)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    Color.Transparent
                            )
                            .padding(16.dp)
                    ) {
                        Text(text = opcion)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onNavigateToGestion) {
                Text("Gestionar")
            }
            Button(onClick = onNavigateToHistorial) {
                Text("Historial")
            }
        }
    }
}

// ----------------------------------------
// dibujo de la ruleta
// ----------------------------------------
@Composable
private fun RuletaGrafica(
    opciones: List<String>,
    seleccion: String?,
    modifier: Modifier = Modifier
) {
    val colores = listOf(
        Color(0xFFB0C4DE),
        Color(0xFFD8BFD8),
        Color(0xFFF5DEB3),
        Color(0xFF98FB98),
        Color(0xFFFFCCCB),
        Color(0xFFE6E6FA)
    )

    Canvas(
        modifier = modifier.size(220.dp)
    ) {
        val total = opciones.size.coerceAtLeast(1)
        val sweep = 360f / total
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        opciones.forEachIndexed { index, opcion ->
            val startAngle = -90f + (index * sweep)
            val baseColor = colores[index % colores.size]
            val fillColor =
                if (opcion == seleccion && seleccion != null)
                    baseColor.copy(alpha = 1f)
                else
                    baseColor.copy(alpha = 0.9f)

            drawArc(
                color = fillColor,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // texto
            val middleAngle = startAngle + sweep / 2f
            val textRadius = radius * 0.6f
            val rad = Math.toRadians(middleAngle.toDouble())
            val textX = center.x + (textRadius * cos(rad)).toFloat()
            val textY = center.y + (textRadius * sin(rad)).toFloat()

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = 28f
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
                    textY + 10f,
                    paint
                )
            }
        }

        // círculo central
        drawCircle(
            color = Color.White,
            radius = radius * 0.25f,
            center = center
        )
    }
}

@Composable
private fun IndicadorRuleta(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(26.dp)
    ) {
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

// ----------------------------------------
// funciones de notificación reales
// ----------------------------------------
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
