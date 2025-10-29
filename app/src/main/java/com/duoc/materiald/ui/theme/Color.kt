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

//Paleta Dark
val DarkColors = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = BackgroundDark,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFEAEAEA),
    onSurface = Color(0xFFEAEAEA)
)