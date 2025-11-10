package com.duoc.materiald // Paquete raíz

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.materiald.ui.screens.PantallaGestion
import com.duoc.materiald.ui.screens.PantallaHistorial
import com.duoc.materiald.ui.screens.PantallaRuleta

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "ruleta"
    ) {
        composable("ruleta") {
            PantallaRuleta(navController)
        }
        composable("gestion") {
            PantallaGestion(navController)
        }
        composable("historial") {
            PantallaHistorial(navController)
        }
    }
}
