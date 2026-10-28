package com.wezen.tiempopormates.ui.theme.Resta

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wezen.tiempopormates.R
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.ui.theme.Suma.SumaScreen
import com.wezen.tiempopormates.ui.theme.theme.BotonAtras
import com.wezen.tiempopormates.ui.theme.theme.BotonCorregir
import com.wezen.tiempopormates.ui.theme.theme.BotonSiguiente
import com.wezen.tiempopormates.ui.theme.theme.BrilloTexto


private const val TAG = "RestaScreen"

@Composable
fun RestaScreen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = false,
    onSiguiente: () -> Unit,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null
) {
    // Crear el ViewModel con el factory
    val factory = RestaViewModelFactory(dbHelper)
    val viewModel: RestaViewModel = viewModel(factory = factory)
    val contexto = LocalContext.current

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    // Cargar usuario al iniciar
    LaunchedEffect(userId) {
        viewModel.CargarConfigUsuario(userId)
        viewModel.CargarUsuario(userId)
        viewModel.generarNuevasRestas()

    }

    Log.d(TAG, "User Viewmodel: ${uiState.user}")

    if (uiState.user != null && uiState.numeros!=null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .padding(start = 16.dp)
        ) {
            // Boton Atras, en caso de ser un Examen no se activa
            if (uiState.user != null && isExamen==false) {
                Spacer(modifier = Modifier.height(24.dp))
                BotonAtras(
                    onClick = {
                        navController.popBackStack()
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .paint(
                        painter = painterResource(id = R.drawable.fondo),
                        contentScale = ContentScale.Crop
                    ),
                contentAlignment = Alignment.Center
            ) {
                BrilloTexto(
                    text = "➖ Resta",
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- ICONO DE LLEVADA ---
            if (uiState.configUser?.llevadasResta == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Llevada",
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            if (uiState.numeros != null && uiState.user != null) {
                val user = uiState.user!!
                val configUser = uiState.configUser!!
                val numeros = uiState.numeros!!

                // DIBUJAMOS LA RESTA
                val str1 = numeros.minuendo.toString().padStart(configUser.numeroCifrasResta, ' ')
                val str2 = numeros.sustraendo.toString().padStart(configUser.numeroCifrasResta, ' ')

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Mostramos la resta
                    MostrarOperacionResta(str1, str2, configUser.numeroCifrasResta)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campos de entrada
                    CamposRespuestaResta(
                        valores = uiState.valores,
                        errores = uiState.erroresPorCifra,
                        numeroCifras = configUser.numeroCifrasResta + 1,
                        onValorCambiado = { indice, valor ->
                            viewModel.actualizarValor(indice, valor)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Texto instructivo
                    Text(
                        text = "Solo un número por casilla",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones
                    if (!uiState.restaCorregida) {
                        BotonCorregir(
                            text = "Corregir",
                            onClick = {
                                viewModel.corregirResta()
                            },
                            enabled = !uiState.isValidating
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            BotonSiguiente(
                                text = if (isExamen) "Siguiente" else "Otra Resta!",
                                onClick = {
                                    if (isExamen) {
                                        // En examen: registrar respuesta y pasar a siguiente
                                        Log.d(TAG, "Registrando respuesta en examen: ${uiState.todosCorrectos}")
                                        onRegistrarRespuesta?.invoke(uiState.todosCorrectos ?: false)
                                        onSiguiente()
                                    } else {
                                        // Fuera de examen: generar nueva suma
                                        viewModel.generarNuevasRestas()
                                    }
                                }
                            )
                        }

                        // Mostrar resultado
                        if (uiState.todosCorrectos == true) {
                            Toast.makeText(
                                contexto,
                                "¡Muy bien! ${user.nombre} Resta correcta ✓",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (uiState.todosCorrectos == false) {
                            val numErrores = uiState.erroresPorCifra.count { it }
                            Toast.makeText(
                                contexto,
                                "Hay $numErrores error${if (numErrores > 1) "es" else ""} marcado${if (numErrores > 1) "s" else ""} en rojo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MostrarOperacionResta(str1: String = "", str2: String = "", numeroCifras: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Espacio vacío en la primera fila
            Box(modifier = Modifier.width(60.dp).height(50.dp))
            // Signo - en la segunda fila
            Box(
                modifier = Modifier.width(60.dp).height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B)
                )
            }
            // Línea separadora
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color.White)
            )
            // Espacio entre la línea y los resultados
            Box(modifier = Modifier.width(60.dp).height(2.dp))
        }

        // COLUMNAS DE NÚMEROS
        repeat(numeroCifras) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // PRIMERA CIFRA
                Box(
                    modifier = Modifier.width(60.dp).height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val char1 = str1.getOrNull(index)
                    if (char1 != null && char1 != ' ') {
                        Text(
                            text = char1.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                // SEGUNDA CIFRA
                Box(
                    modifier = Modifier.width(60.dp).height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val char2 = str2.getOrNull(index)
                    if (char2 != null && char2 != ' ') {
                        Text(
                            text = char2.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                // LÍNEA SEPARADORA
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(Color.White)
                )
            }
        }
    }
}

@Composable
private fun CamposRespuestaResta(
    valores: List<String>,
    errores: List<Boolean>,
    numeroCifras: Int,
    onValorCambiado: (Int, String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        for (index in 0 until numeroCifras) {
            // CASILLA PARA RESULTADO
            OutlinedTextField(
                value = valores.getOrNull(index) ?: "",
                onValueChange = { onValorCambiado(index, it)
                                Log.d(TAG, "Valor cambiado: $it en la casilla $index      Nº Cifras REal: $numeroCifras") },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errores.getOrNull(index) == true)
                        Color(0xFFFF0000) else Color(0xFF6C4A4A),
                    unfocusedBorderColor = if (errores.getOrNull(index) == true)
                        Color(0xFFFF0000) else Color(0xFF6C4A4A),
                    cursorColor = Color(0xFF2196F3)
                ),
                modifier = Modifier
                    .width(50.dp)
                    .height(60.dp)
            )
        }
    }
}

//Este es el Composable que llamaremos desde el Examen
@Composable
fun RestaScreenExamen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = true,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null,
    onSiguiente: () -> Unit = {}
) {
    RestaScreen(
        userId= userId,
        navController = navController,
        dbHelper = dbHelper,
        isExamen = isExamen,
        onRegistrarRespuesta = onRegistrarRespuesta,
        onSiguiente = onSiguiente
    )

}