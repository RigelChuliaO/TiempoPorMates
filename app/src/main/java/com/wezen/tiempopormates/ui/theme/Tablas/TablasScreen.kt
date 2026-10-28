package com.wezen.tiempopormates.ui.theme.Tablas

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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

private const val TAG = "TablasScreen"

@Composable
fun TablasScreen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = false,
    onSiguiente: () -> Unit,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null
) {
    // Crear el ViewModel con el factory
    val factory = TablasViewModelFactory(dbHelper)
    val viewModel: TablasViewModel = viewModel(factory = factory)
    val contexto = LocalContext.current

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    // Cargar usuario al iniciar
    LaunchedEffect(userId) {
        if (userId > 0) {
            Log.d(TAG, "Iniciando carga de datos para userId: $userId")
            viewModel.CargarUsuario(userId)
            viewModel.CargarConfigUsuario(userId)
        } else {
            Log.e(TAG, "userId inválido: $userId")
        }
    }

    // Generar operación cuando tengamos usuario Y configuración
    LaunchedEffect(uiState.user, uiState.configUser) {
        if (uiState.user != null && uiState.configUser != null && uiState.operacionTabla == null) {
            Log.d(TAG, "Generando primera operación")
            viewModel.generarNuevaOperacionTabla()
        }
    }

    Log.d(TAG, "UserId Recibido: $userId")
    Log.d(TAG, "UserId Operaciones: ${uiState.operacionTabla}")

    Log.d(TAG, "User Viewmodel: ${uiState.user}")

    if (uiState.user != null && uiState.operacionTabla != null) {
        val user = uiState.user!!
        val operacioTabla = uiState.operacionTabla!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
            ,
        ) {
            /// Boton Atras, en caso de ser un Examen no se activa
            if (isExamen==false) {
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
                    text = "📋 Tablas",
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dibujamos la operación seleccionada
            Box(
                modifier = Modifier
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text( // Tabla seleccionada
                        text = "${operacioTabla.tabla} ",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "x",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Text( // Multiplicador de la tabla
                        text = "${operacioTabla.multiplicador} ",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Caja de texto para escribir la solución
            OutlinedTextField(
                value = uiState.respuesta,
                onValueChange = { nuevo ->
                    viewModel.actualizarRespuesta(nuevo)

                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.letratiza))
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                // Color del marco en función de si es correcto cuando lo corrija
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C4A4A),
                    unfocusedBorderColor = Color(0xFF6C4A4A) ,
                    cursorColor = Color(0xFF2196F3)
                ),
                modifier = Modifier
                    .width(100.dp)
                    .height(60.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)

            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            if (!uiState.operacionTablaCorregida) {
                // Botones
                    BotonCorregir(
                        text = "Corregir",
                        onClick = {
                            viewModel.corregirOperacionTabla()
                        },
                        enabled = !uiState.isValidating
                    )
            } else {
                // Botones cuando el resultado de la tabla es correcto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BotonSiguiente(
                        text = if (isExamen) "Siguiente Ejercicio" else "Otra!",
                        onClick = { if (isExamen) {
                            // En examen: registrar respuesta y pasar a siguiente
                            Log.d(TAG, "Registrando respuesta en examen: ${uiState.esCorrecta}")
                            onRegistrarRespuesta?.invoke(uiState.esCorrecta ?: false)
                            onSiguiente()
                        } else {
                            // Fuera de examen: generar nueva suma
                            viewModel.generarNuevaOperacionTabla()
                        }}
                    )
                }
                // Mostrar resultado
                if (uiState.esCorrecta == true) {
                    Toast.makeText(
                        contexto,
                        "¡Muy bien! ${user.nombre} correcta ✓",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (uiState.esCorrecta == false) {
                    Toast.makeText(
                        contexto,
                        "No es correcto ${user.nombre} La respuesta correcta era: ${uiState.operacionTabla?.resultado}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }/* else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }*/
}

@Composable
fun TablasScreenExamen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = true,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null,
    onSiguiente: () -> Unit = {}
) {
    TablasScreen(
        userId= userId,
        navController = navController,
        dbHelper = dbHelper,
        isExamen = isExamen,
        onRegistrarRespuesta = onRegistrarRespuesta,
        onSiguiente = onSiguiente
    )

}