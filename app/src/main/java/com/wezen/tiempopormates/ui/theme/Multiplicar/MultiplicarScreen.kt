package com.wezen.tiempopormates.ui.theme.Multiplicar

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wezen.tiempopormates.R
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.ui.theme.Suma.SumaScreen
import com.wezen.tiempopormates.ui.theme.theme.BotonAtras
import com.wezen.tiempopormates.ui.theme.theme.BotonCorregir
import com.wezen.tiempopormates.ui.theme.theme.BotonSiguiente
import com.wezen.tiempopormates.ui.theme.theme.BrilloTexto

private const val TAG = "MultiplicarScreen"

@Composable
fun MultiplicarScreen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = false,
    onSiguiente: () -> Unit,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null
) {
    // Crear el ViewModel con el factory
    val factory = MultiplicarViewModelFactory(dbHelper)
    val viewModel: MultiplicarViewModel = viewModel(factory = factory)
    val contexto = LocalContext.current

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    // Cargar usuario al iniciar
    LaunchedEffect(userId) {
        viewModel.CargarUsuario(userId)
        viewModel.CargarConfigUsuario(userId)
        viewModel.generarNuevasMultiplicaciones()
    }

    Log.d(TAG, "User Viewmodel: ${uiState.user}")

    if (uiState.user != null && uiState.numeros!=null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .padding(start = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Boton Atras, en caso de ser un Examen no se activa
            if (uiState.user != null && isExamen == false) {
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
                    text = "✖️ Multiplicar",
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val configUser = uiState.configUser!!
            val numeros = uiState.numeros!!


            // DIBUJAMOS LA MULTIPLICACIÓN
            val str1 = numeros.multiplicando.toString().padStart(configUser.numeroCifrasMultiplicando, ' ')
            val str2 = numeros.multiplicador.toString().padStart(configUser.numeroCifrasMultiplicador, ' ')

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // LÍNEA 1: MULTIPLICANDO
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(configUser.numeroCifrasMultiplicando) { index ->
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val char = str1.getOrNull(index)
                            if (char != null && char != ' ') {
                                Text(
                                    text = char.toString(),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // LÍNEA 2: MULTIPLICADOR (alineado a la derecha)
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "✖️",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                    }


                    repeat(configUser.numeroCifrasMultiplicador) { index ->
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val char = str2.getOrNull(index)
                            if (char != null && char != ' ') {
                                Text(
                                    text = char.toString(),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // LÍNEA SEPARADORA DE LA MULTIPLICACIÓN CON LA OPERACIÓN
                HorizontalDivider(
                    color = Color.White,
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Creación de cuadros para realizar la operación, operaciones parciales
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Column {
                        repeat(configUser.numeroCifrasMultiplicador) { indexMultiplicador ->
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = ((indexMultiplicador) * 46).dp)
                            ) {
                                repeat(configUser.numeroCifrasMultiplicando + 1) { indexCifra ->
                                    val indexValor =
                                        indexMultiplicador * (configUser.numeroCifrasMultiplicando + 1) + indexCifra
                                    OutlinedTextField(
                                        value = uiState.valores.getOrNull(indexValor) ?: "",
                                        onValueChange = { nuevo ->
                                            viewModel.actualizarValor(indexValor, nuevo)
                                        },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = if (uiState.erroresIntermedios.getOrNull(
                                                    indexValor
                                                ) == true
                                            )
                                                Color(0xFFFF0000) else Color(0xFF6C4A4A),
                                            unfocusedBorderColor = if (uiState.erroresIntermedios.getOrNull(
                                                    indexValor
                                                ) == true
                                            )
                                                Color(0xFFFF0000) else Color(0xFF6C4A4A),
                                            cursorColor = Color(0xFF2196F3)
                                        ),
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(60.dp)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // LÍNEA SEPARADORA DE LA OPERACIÓN CON LA SOLUCIÓN
                HorizontalDivider(
                    color = Color.White,
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // CASILLAS PARA LA SOLUCIÓN
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(configUser.numeroCifrasMultiplicando + configUser.numeroCifrasMultiplicador) { index ->
                        Column {
                            OutlinedTextField(
                                value = uiState.valoresResultado.getOrNull(index) ?: "",
                                onValueChange = { nuevo ->
                                    viewModel.actualizarValorResultado(index, nuevo)
                                },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (uiState.erroresResultado.getOrNull(
                                            index
                                        ) == true
                                    )
                                        Color(0xFFFF0000) else Color(0xFF6C4A4A),
                                    unfocusedBorderColor = if (uiState.erroresResultado.getOrNull(
                                            index
                                        ) == true
                                    )
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

                // Texto instructivo
                Text(
                    text = "Solo un número por casilla",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!uiState.multiplicacionCorregida) {
                    BotonCorregir(
                        text = "Corregir",
                        onClick = {
                            viewModel.corregirMultiplicacion()
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
                            text = if (isExamen) "Siguiente ejercicio" else "Otra Multiplicación!",
                            onClick = {
                                if (isExamen) {
                                    // En examen: registrar respuesta y pasar a siguiente
                                    Log.d(TAG, "Registrando respuesta en examen: ${uiState.todosCorrectos}")
                                    onRegistrarRespuesta?.invoke(uiState.todosCorrectos ?: false)
                                    viewModel.generarNuevasMultiplicaciones()
                                    onSiguiente()
                                } else {
                                    // Fuera de examen: generar nueva multiplicacion
                                    viewModel.generarNuevasMultiplicaciones()
                                }
                            }
                        )
                    }
                }

                // Mostrar resultado
                if (uiState.todosCorrectos == true) {
                    Toast.makeText(contexto,"¡Muy bien! ${uiState.user!!.nombre} Multiplicación correcta ✓",Toast.LENGTH_SHORT).show()
                } else if (uiState.todosCorrectos == false) {
                    val numErrores = uiState.erroresResultado.count { it }
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

//Este es el Composable que llamaremos desde el Examen
@Composable
fun MultiplicacionScreenExamen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = true,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null,
    onSiguiente: () -> Unit = {}
) {
    MultiplicarScreen(
        userId= userId,
        navController = navController,
        dbHelper = dbHelper,
        isExamen = isExamen,
        onRegistrarRespuesta = onRegistrarRespuesta,
        onSiguiente = onSiguiente
    )

}