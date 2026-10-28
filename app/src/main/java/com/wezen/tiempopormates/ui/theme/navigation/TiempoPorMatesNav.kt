package com.wezen.tiempopormates.ui.theme.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.Gemini.GeminiServiceImpl
import com.wezen.tiempopormates.ui.theme.Comprension.ComprensionScreen
import com.wezen.tiempopormates.ui.theme.Configuracion.ConfiguracionScreen
import com.wezen.tiempopormates.ui.theme.Dividir.DividirScreen
import com.wezen.tiempopormates.ui.theme.Estadisticas.EstadisticasScreen
import com.wezen.tiempopormates.ui.theme.Examenes.ExamenScreen
import com.wezen.tiempopormates.ui.theme.Inicio.InicioScreen
import com.wezen.tiempopormates.ui.theme.Menu.MenuScreen
import com.wezen.tiempopormates.ui.theme.Multiplicar.MultiplicarScreen
import com.wezen.tiempopormates.ui.theme.Resta.RestaScreen
import com.wezen.tiempopormates.ui.theme.Suma.SumaScreen
import com.wezen.tiempopormates.ui.theme.Tablas.TablasScreen

import kotlin.text.toLongOrNull

@Composable
fun TiempoPorMatesNavigation(dbHelper: DatabaseHelper) {
    val navController = rememberNavController()
    //val tpmNavViewModel: TPMNavViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        composable("inicio") {
        InicioScreen(
            dbHelper = dbHelper,
            navController = navController
            )
        }

        composable("menu/{userId}") {
            backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            Log.d("TiempoPorMatesNavigation", "userId: $userId")
            MenuScreen(
                dbHelper = dbHelper,
                userId = userId,
                navController = navController
            )
        }
        composable("suma/{userId}") {
                backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            Log.d("TiempoPorMatesNavigation", "userId: $userId")
            SumaScreen(
                userId = userId,
                dbHelper = dbHelper,
                navController = navController,
                isExamen = false,
                onSiguiente = { }
            )
        }
        composable("resta/{userId}") {
                backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            Log.d("TiempoPorMatesNavigation", "userId: $userId")
            RestaScreen(
                userId = userId,
                dbHelper = dbHelper,
                navController = navController,
                isExamen = false,
                onSiguiente = { }
            )
        }
        composable("multiplicar/{userId}") {
                backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            Log.d("TiempoPorMatesNavigation", "userId: $userId")
            MultiplicarScreen(
                userId = userId,
                dbHelper = dbHelper,
                navController = navController,
                isExamen = false,
                onSiguiente = { }
            )
        }

        composable("tablas/{userId}") {
                backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            Log.d("TiempoPorMatesNavigation", "userId: $userId")
            TablasScreen(
                userId = userId ,
                dbHelper = dbHelper,
                navController = navController,
                isExamen = false,
                onSiguiente = { }
            )
        }

        composable("config/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            // Obtener el usuario completo
            if (userId != null) {
                ConfiguracionScreen(
                    userId = userId,
                    dbHelper = dbHelper,
                    navController = navController,
                )
            }
        }

        composable("Comprension/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            val user = dbHelper.ObternerUserById(userId)
            if (user != null) {
                ComprensionScreen(
                    userId = userId,
                    dbHelper = dbHelper,
                    navController = navController,
                    isExamen = false,
                    onSiguiente = { }

                )
            }
        }
        composable("Estadisticas/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            val user = dbHelper.ObternerUserById(userId)
            if (user != null) {
                EstadisticasScreen(
                    userId = userId,
                    dbHelper = dbHelper,
                    navController = navController
                    )
            }
        }
        composable("Dividir/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            // Obtener el usuario completo
            val user = dbHelper.ObternerUserById(userId)
            if (user != null) {
                DividirScreen(
                    userId = userId,
                    dbHelper = dbHelper,
                    navController = navController,
                    isExamen = false,
                    onSiguiente = { }
                )
            }
        }

        composable("Examen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0
            val user = dbHelper.ObternerUserById(userId)
            if (user != null) {
                ExamenScreen(
                    userId = userId,
                    navController = navController,
                    dbHelper = dbHelper,
                )
            }
        }


    }
}