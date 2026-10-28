package com.wezen.tiempopormates

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.ui.theme.navigation.TiempoPorMatesNavigation
import com.wezen.tiempopormates.ui.theme.theme.TiempoPorMatesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = DatabaseHelper.getInstance(this)

        // Esto inicializa la base si no existe aún
        val db = dbHelper.writableDatabase

        print(dbHelper.getAllUsers())

        setContent {
            TiempoPorMatesTheme {
                Surface (
                    modifier= Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    Log.d(("MainActivity"), "empezamos¡")
                    TiempoPorMatesNavigation(dbHelper)
                }
            }
        }
    }
}