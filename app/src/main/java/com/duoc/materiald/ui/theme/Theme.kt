package com.duoc.materiald.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    themeName: String = "Clásico",
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (themeName) {
        "Oscuro" -> DarkColors
        "Pastel" -> PastelColors
        else -> if (useDarkTheme) DarkColors else LightColors // Clásico
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )

}


