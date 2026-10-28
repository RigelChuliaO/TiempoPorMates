package com.wezen.tiempopormates.ui.theme.Examenes


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.Gemini.GeminiServiceImpl
import com.wezen.tiempopormates.data.entity.PruebaExamen
import com.wezen.tiempopormates.data.entity.ResultadosExamen
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.ExamenUsesCases
import com.wezen.tiempopormates.ui.theme.Comprension.ComprensionScreenExamen
import com.wezen.tiempopormates.ui.theme.Dividir.DividirScreenExamen
import com.wezen.tiempopormates.ui.theme.Multiplicar.MultiplicacionScreenExamen
import com.wezen.tiempopormates.ui.theme.Resta.RestaScreenExamen
import com.wezen.tiempopormates.ui.theme.Suma.SumaScreenExamen
import com.wezen.tiempopormates.ui.theme.Tablas.TablasScreenExamen


private const val TAG = "ExamenScreen"

@Composable
fun ExamenScreen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    onFinalizarExamen: (resultados: ResultadosExamen) -> Unit = {},

) {
    val factory = ExamenViewModelFactory(UserRepository(dbHelper), dbHelper, ExamenUsesCases())
    val viewModel: ExamenViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    // Cargar usuario e inicializar examen
    LaunchedEffect(userId) {
        viewModel.CargarConfigUsuario(userId)
        viewModel.CargarUsuario(userId)
        viewModel.GenerarPruebasExamen()
    }

    Log.d(TAG, "Userid: $userId")
    Log.d(TAG, "uiState: $uiState")

    if (uiState.isLoading) {
        // Pantalla de carga
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2196F3))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Preparando examen...",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

    } else
        Log.d(TAG, "Aqui llego ${uiState.user != null} y  ${uiState.pruebas.isNotEmpty()}", )
        if (uiState.userid != null && uiState.pruebas.isNotEmpty()) {
            Log.d(TAG, "Aqui llego2 ${uiState.user != null} y  ${uiState.pruebas.isNotEmpty()}", )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A))
            ) {

                if (!uiState.examenFinalizado) {
                    // MOSTRAR PRUEBA ACTUAL
                    ExamenHeader(
                        indiceActual = uiState.indicePruebaActual,
                        totalPruebas = uiState.configUser!!.numeroPreguntasExamen,
                        onBack = { navController.popBackStack() }
                    )

                    LinearProgressIndicator(
                        progress = { (uiState.indicePruebaActual + 1).toFloat() / uiState.configUser!!.numeroPreguntasExamen },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = Color(0xFF2196F3),
                        trackColor = Color(0xFF424242)
                    )

                    val pruebaActual = viewModel.obtenerPruebaActual()
                    if (pruebaActual != null) {
                        PruebaExamenContent(
                            pruebaActual = pruebaActual,
                            user = uiState.user!!,
                            navController = navController,
                            dbHelper = dbHelper,
                            viewModel = viewModel,
                            onRegistrarRespuesta = { esCorrecto ->
                                viewModel.registrarRespuesta(esCorrecto)
                            },
                            onSiguiente = {
                                viewModel.siguientePrueba()  // Necesitas implementar esta función
                            }
                        )
                    }
                } else {
                    // MOSTRAR RESULTADOS
                    ResultadosExamenScreen(
                        pruebas = uiState.pruebas,
                        resultados = uiState.resultados!!,
                        yaGuardado = uiState.yaGuardado,
                        onContinuar = {
                            if (!uiState.yaGuardado) {
                                viewModel.guardarResultadosEnBD()
                                navController.popBackStack()
                            }
                            onFinalizarExamen(uiState.resultados!!)
                        },
                        onBack = { navController.popBackStack()
                        }
                    )
                }
            }

        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "Error desconocido",
                    color = Color(0xFFFF6B6B),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
}

@Composable
private fun ExamenHeader(
    indiceActual: Int,
    totalPruebas: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Text("⬅️", fontSize = 24.sp)
        }
        Text(
            text = "Ejercicio ${indiceActual + 1}/$totalPruebas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Box(modifier = Modifier.width(40.dp))
    }
}

@Composable
private fun PruebaExamenContent(
    pruebaActual: PruebaExamen,
    user: User,
    navController: NavController,
    dbHelper: DatabaseHelper,
    viewModel: ExamenViewModel,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null,  // Agregar
    onSiguiente: (() -> Unit)? = null  // Agregar
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (pruebaActual.modulo) {
            "suma" -> SumaScreenExamen(
                userId = user.id,
                navController = navController,
                dbHelper = dbHelper,
                isExamen = true,
                onRegistrarRespuesta = onRegistrarRespuesta,
                onSiguiente = {viewModel.siguientePrueba()}
            )
            "resta" -> RestaScreenExamen(
                userId = user.id,
                navController = navController,
                dbHelper = dbHelper,
                isExamen = true,
                onRegistrarRespuesta = onRegistrarRespuesta,
                onSiguiente = {
                    viewModel.siguientePrueba()
                }
            )
            "multiplicar" -> MultiplicacionScreenExamen(
                userId = user.id,
                navController = navController,
                dbHelper = dbHelper,
                isExamen = true,
                onRegistrarRespuesta = onRegistrarRespuesta,
                onSiguiente = {
                    viewModel.siguientePrueba()
                }
            )
            "tablas" -> TablasScreenExamen(
                userId = user.id,
                navController = navController,
                dbHelper = dbHelper,
                isExamen = true,
                onRegistrarRespuesta = onRegistrarRespuesta,
                onSiguiente = {
                    viewModel.siguientePrueba()
                }
            )
            "dividir" -> DividirScreenExamen(
                userId = user.id,
                navController = navController,
                dbHelper = dbHelper,
                isExamen = true,
                onRegistrarRespuesta = onRegistrarRespuesta,
                onSiguiente = {
                    viewModel.siguientePrueba()
                }
            )
            "comprension" -> ComprensionScreenExamen(
                userId = user.id,
                navController = navController,
                dbHelper = dbHelper,
                isExamen = true,
                onRegistrarRespuesta = onRegistrarRespuesta,
                onSiguiente = {
                    viewModel.siguientePrueba()
                }
            )
            else -> {
                Text(
                    text = "Tipo de prueba no soportada: ${pruebaActual.modulo}",
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun ResultadosExamenScreen(
    pruebas: List<PruebaExamen>,
    resultados: ResultadosExamen,
    yaGuardado: Boolean,
    onContinuar: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Examen Finalizado",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Círculo con porcentaje
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(
                    color = when {
                        resultados.porcentajeAcierto >= 80 -> Color(0xFF4CAF50)
                        resultados.porcentajeAcierto >= 60 -> Color(0xFF2196F3)
                        else -> Color(0xFFFF9800)
                    },
                    shape = RoundedCornerShape(100.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${resultados.porcentajeAcierto}%",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Correctas: ${resultados.pruebasCorrectas}",
            fontSize = 20.sp,
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "Incorrectas: ${resultados.pruebasIncorrectas}",
            fontSize = 20.sp,
            color = Color(0xFFFF9800),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Desglose por operación
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Desglose por operación:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mapeo de operaciones a nombre y emoji
            val operacionesInfo = mapOf(
                "suma" to ("Suma" to "➕"),
                "resta" to ("Resta" to "➖"),
                "multiplicar" to ("Multiplicación" to "✖️"),
                "dividir" to ("División" to "➗"),
                "tablas" to ("Tablas" to "🔢"),
                "comprension" to ("Comprensión" to "🧠")
            )

            operacionesInfo.forEach { (operacion, infoOperacion) ->
                val (nombre, emoji) = infoOperacion
                val correctas = pruebas.filter { it.modulo == operacion && it.esCorrecto }.size
                val total = pruebas.filter { it.modulo == operacion }.size

                if (total > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$emoji $nombre:",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$correctas/$total",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$emoji $nombre:",
                            color = Color(0xFF666666),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "0/0",
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                Log.d(TAG, "yaGuardado: $yaGuardado")
                if (!yaGuardado) {
                    Log.d(TAG, "Guardando resultados...")
                    onContinuar()
                } else {
                    Log.d(TAG, "Ya guardado, volviendo atrás...")
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text(
                "Continuar",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}