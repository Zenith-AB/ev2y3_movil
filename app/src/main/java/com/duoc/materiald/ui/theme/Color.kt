package com.duoc.materiald.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

//Paleta básica para mi aplicación
val Primary = Color(0xFF3A7AFE)
val Secondary = Color(0xFF00C2A8)
val BackgroundLight= Color(0xFFF7F9FC)
val BackgroundDark= Color(0xFF121212)

//Paleta Light
val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF111318),
    onSurface = Color(0xFF111318)
)

//Paleta Dark (mejorada)
val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9), // Azul suave
    secondary = Color(0xFF80CBC4), // Verde azulado
    tertiary = Color(0xFFCE93D8), // Púrpura suave
    background = Color(0xFF121212), // Negro profundo
    surface = Color(0xFF1E1E1E), // Gris oscuro
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFFE0E0E0), // Gris claro
    onSurface = Color(0xFFE0E0E0)
)

// Paleta Pastel (mejorada para mejor contraste)
val PastelColors = lightColorScheme(
    primary = Color(0xFFFF9AA2), // Rosa pastel más saturado
    secondary = Color(0xFF85E3C1), // Verde menta
    tertiary = Color(0xFFB4A7D6), // Lavanda
    background = Color(0xFFFFFAF5), // Blanco cálido
    surface = Color(0xFFFFFFFF), // Blanco puro
    onPrimary = Color(0xFF4A1C1C), // Marrón oscuro
    onSecondary = Color(0xFF1C4A3A), // Verde oscuro
    onTertiary = Color(0xFF2D2640), // Púrpura oscuro
    onBackground = Color(0xFF2D2D2D), // Gris oscuro
    onSurface = Color(0xFF2D2D2D) // Gris oscuro
)