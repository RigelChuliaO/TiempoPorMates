package com.wezen.tiempopormates.ui.theme.Suma


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
import com.wezen.tiempopormates.data.Gemini.GeminiServiceImpl
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.ui.theme.theme.BotonAtras
import com.wezen.tiempopormates.ui.theme.theme.BotonCorregir
import com.wezen.tiempopormates.ui.theme.theme.BotonSiguiente
import com.wezen.tiempopormates.ui.theme.theme.BrilloTexto


private const val TAG = "SumaScreen"


@Composable
fun SumaScreen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = false,
    onSiguiente: () -> Unit,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null
) {

    // Crear el ViewModel con el factory
    val factory = SumaViewModelFactory(dbHelper)
    val viewModel: SumaViewModel = viewModel(factory = factory)
    val contexto = LocalContext.current

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    Log.d(TAG, "UserId Recibido: $userId")

    // Cargar usuario al iniciar

    LaunchedEffect(userId) {

        viewModel.CargarConfigUsuario(userId)
        viewModel.CargarUsuario(userId)
        viewModel.generarNuevasSumas()

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
                navController.navigate("menu/${uiState.user!!.id}")
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
                text = "➕ Suma",
                color = Color(0xFF88E834)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

            // --- ICONO DE LLEVADA ---
            if (uiState.configUser!!.llevadasSuma) {
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

            if (uiState.isLoading) {//Si esta cargando muestra un circulo
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            val user = uiState.user!!
            val configUser = uiState.configUser!!
            val numeros = uiState.numeros!!
            val str1 = numeros.sumando1.toString().padStart(configUser.numeroCifrasSuma, ' ')
            val str2 = numeros.sumando2.toString().padStart(configUser.numeroCifrasSuma, ' ')

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                //Dibjuamos la suma
                MostrarOperacionSuma(str1, str2, configUser.numeroCifrasSuma)

                Spacer(modifier = Modifier.height(16.dp))

                // Campos de entrada
                CamposRespuestaSuma(
                    valores = uiState.valores,
                    errores = uiState.erroresPorCifra,
                    numeroCifras = configUser.numeroCifrasSuma + 1,
                    onValorCambiado = { indice, valor ->
                        viewModel.actualizarValor(indice, valor)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Botones
                if (!uiState.sumaCorregida) {
                    BotonCorregir(
                        text = "Corregir",
                        onClick = {
                            viewModel.corregirSuma()
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
                            text = if (isExamen) "Siguiente Ejercicio" else "Otra Suma!",
                            onClick = {
                                if (isExamen) {
                                    // En examen: registrar respuesta y pasar a siguiente
                                    Log.d(TAG, "Registrando respuesta en examen: ${uiState.todosCorrectos}")
                                    onRegistrarRespuesta?.invoke(uiState.todosCorrectos ?: false)
                                    onSiguiente()
                                } else {
                                    // Fuera de examen: generar nueva suma
                                    viewModel.generarNuevasSumas()
                                }
                            }
                        )
                    }
                    // Mostrar resultado
                    if (uiState.todosCorrectos == true) {
                        Toast.makeText(
                            contexto,
                            "¡Muy bien! ${user.nombre} Suma correcta ✓",
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

@Composable
private fun MostrarOperacionSuma(str1: String ="", str2: String="", numeroCifras: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.width(60.dp).height(50.dp))
            Box(
                modifier = Modifier.width(60.dp).height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color.White)
            )
            Box(modifier = Modifier.width(60.dp).height(2.dp))
        }

        repeat(numeroCifras) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
private fun CamposRespuestaSuma(
    valores: List<String>,
    errores: List<Boolean>,
    numeroCifras: Int,
    onValorCambiado: (Int, String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        repeat(numeroCifras) { index ->
            OutlinedTextField(
                value = valores.getOrNull(index) ?: "",
                onValueChange = { onValorCambiado(index, it) },
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
                    focusedBorderColor = if (errores.getOrNull(index) == true) Color.Red else Color(0xFF6C4A4A),
                    unfocusedBorderColor = if (errores.getOrNull(index) == true) Color.Red else Color(0xFF6C4A4A),
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
fun SumaScreenExamen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = true,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null,
    onSiguiente: () -> Unit = {}
) {
    SumaScreen(
        userId= userId,
        navController = navController,
        dbHelper = dbHelper,
        isExamen = isExamen,
        onRegistrarRespuesta = onRegistrarRespuesta,
        onSiguiente = onSiguiente
    )

}