package com.wezen.tiempopormates.ui.theme.Dividir

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.navigation.NavController
import com.wezen.tiempopormates.R
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.ui.theme.Suma.SumaScreen
import com.wezen.tiempopormates.ui.theme.theme.BotonAtras
import com.wezen.tiempopormates.ui.theme.theme.BotonCorregir
import com.wezen.tiempopormates.ui.theme.theme.BotonSiguiente
import com.wezen.tiempopormates.ui.theme.theme.BrilloTexto
import kotlin.math.abs


private const val TAG = "DividirScreen"

@Composable
fun DividirScreen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = false,
    onSiguiente: () -> Unit,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null

){

    // Crear el ViewModel con el factory
    val factory = DividirViewModelFactory(dbHelper)
    val viewModel: DividirViewModel = viewModel(factory = factory)
    val contexto = LocalContext.current

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    Log.d(TAG, "UserId Recibido: $userId")


    LaunchedEffect(userId) {
        viewModel.CargarConfigUsuario(userId)
        viewModel.CargarUsuario(userId)
        viewModel.generarNuevaDivision()

    }
    //Simpre confirmamos qu eno tenemos un usuario o datos nulos para dibujar
    if (uiState.user != null && uiState.DatosDivision != null) {

        val user = uiState.user!!
        val configUser = uiState.configUser!!
        val datosDivision = uiState.DatosDivision!!
        val cifrasCociente = datosDivision.cociente.toString().length
        val cifrasDividendo = datosDivision.dividendo.toString().length
        val cifrasDivisor = datosDivision.divisor.toString().length
        val colsMatriz = cifrasCociente * 2


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(start = 16.dp)
        ) {
            // Boton Atras, en caso de ser un Examen no se activa
            if (uiState.configUser != null && isExamen==false) {
                Spacer(modifier = Modifier.height(24.dp))
                BotonAtras(
                    onClick = {
                        navController.navigate("menu/${uiState.configUser!!.id}")
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
                    text = "➗ División",
                    color = Color(0xFF88E834)
                )
            }

            //---------------------------------DIVISION-----------------------------------------------------------------------------
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()

            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                )
                {
                    //DIVIDENDO
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(configUser.numeroCifrasDividendo) { index ->
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val char = datosDivision.dividendo.toString().getOrNull(index)
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
                        // DIVISOR con bordes especiales
                        Column {
                            Row {
                                repeat(cifrasDivisor) { index ->
                                    Box(
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(60.dp)
                                            .drawBehind {
                                                // Línea izquierda (solo en la primera)
                                                if (index == 0) {
                                                    drawLine(
                                                        color = Color.White,
                                                        start = Offset(0f, 0f),
                                                        end = Offset(0f, size.height),
                                                        strokeWidth = 2.dp.toPx()
                                                    )
                                                }
                                                // Línea inferior
                                                drawLine(
                                                    color = Color.White,
                                                    start = Offset(0f, size.height),
                                                    end = Offset(size.width, size.height),
                                                    strokeWidth = 2.dp.toPx()
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (index < cifrasDivisor) datosDivision.divisor.toString()[index].toString() else " ",
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                if (cifrasCociente>cifrasDivisor){ //Para ajustar si cociente y divsor no tienen las mismas cifras
                                    repeat(abs(cifrasCociente-cifrasDivisor)) {
                                        Box(
                                            modifier = Modifier
                                                .width(50.dp)
                                                .height(60.dp)
                                                .drawBehind {
                                                    drawLine(
                                                        color = Color.White,
                                                        start = Offset(0f, size.height),
                                                        end = Offset(size.width, size.height),
                                                        strokeWidth = 2.dp.toPx()
                                                    )
                                                }

                                            )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    //CANVAS
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    ) {

                        Column {
                            repeat(colsMatriz) { indexDividendo ->
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .fillMaxWidth()

                                ) {//-------------------CANVAS DE OPERACIONES------------------------------------
                                    repeat(cifrasDividendo) { indexCifra ->
                                        val indexValor =
                                            indexDividendo * (cifrasDividendo) + indexCifra
                                        OutlinedTextField(
                                            value = uiState.valoresUsuarioCanvas.getOrNull(indexValor) ?: "",//"${indexValor}",//.valoresUsuarioCanvas[indexValor],//uiState.valoresUsuarioCanvas.getOrNull(indexValor) ?: "",
                                            onValueChange = { nuevo ->
                                               viewModel.actualizarValorCanvas(indexValor,nuevo)
                                            },
                                            singleLine = true,
                                            textStyle = androidx.compose.ui.text.TextStyle(
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = if (uiState.erroresCanvas.getOrNull(
                                                        indexValor
                                                    ) == true
                                                )
                                                    Color(0xFFFF0000) else Color(0xFF6C4A4A),
                                                unfocusedBorderColor = if (uiState.erroresCanvas.getOrNull(
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
                                    Spacer(modifier = Modifier.width(8.dp))

                                    if (indexDividendo == 0) { //Pinto los cuadraditos del cociente
                                        Spacer(modifier = Modifier.height(8.dp))
                                        //---------------------COCIENTE----------------------------------------------------------------
                                        repeat(cifrasCociente) {
                                            Box(
                                                modifier = Modifier
                                                    .width(50.dp)
                                                    .height(60.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = uiState.valoresCociente.getOrNull(it) ?: "",//),.getOrNull(index) ?: "",
                                                    onValueChange = { nuevo ->
                                                        viewModel.actualizarValorCociente(it,nuevo)
                                                    },
                                                    singleLine = true,
                                                    textStyle = androidx.compose.ui.text.TextStyle(
                                                        fontSize = 20.sp,
                                                        textAlign = TextAlign.Center,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    ),
                                                    keyboardOptions = KeyboardOptions.Default.copy(
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = if (uiState.erroresCociente.getOrNull(
                                                                it
                                                            ) == true
                                                        )
                                                            Color(0xFFFF0000) else Color(0xFF6C4A4A),
                                                        unfocusedBorderColor = if (uiState.erroresCociente.getOrNull(
                                                                it
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
                                        if (cifrasDivisor>cifrasCociente){ //Para ajustar si cociente y divsor no tienen las mismas cifras
                                            repeat(abs(cifrasDivisor-cifrasCociente)) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(50.dp)
                                                        .height(60.dp)
                                                )
                                            }
                                        }

                                    } else { //Resto de filas para alinearlo
                                        repeat(maxOf(cifrasCociente,cifrasDivisor)) {
                                            Box(
                                                modifier = Modifier
                                                    .width(50.dp)
                                                    .height(60.dp)
                                            ) {}
                                        }
                                    }

                                }

                            }


                        }


                    }


                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones
            if (!uiState.divisionCorregida) {
                BotonCorregir(
                    text = "Corregir",
                    onClick = {

                        viewModel.corregirDivision()
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
                        text = if (isExamen) "Siguiente Ejercicio" else "Otra Division!",
                        onClick = {
                            if (isExamen) {
                                // En examen: registrar respuesta y pasar a siguiente
                                android.util.Log.d(TAG, "Registrando respuesta en examen: ${uiState.todosCorrectos}")
                                onRegistrarRespuesta?.invoke(uiState.todosCorrectos ?: false)
                                onSiguiente()
                            } else {
                                // Fuera de examen: generar nueva suma
                                viewModel.generarNuevaDivision()
                            }
                        }
                    )
                }
                // Mostrar resultado
                if (uiState.todosCorrectos == true) {
                    Toast.makeText(
                        contexto,
                        "¡Muy bien! ${user.nombre} Division correcta ✓",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (uiState.todosCorrectos == false) {
                    val numErrores = uiState.erroresPorCifra.count { it }
                    Toast.makeText(
                        contexto,
                        "Lo siento ${user.nombre} no es correcta",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

//Este es el Composable que llamaremos desde el Examen
@Composable
fun DividirScreenExamen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = true,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null,
    onSiguiente: () -> Unit = {}
) {
    DividirScreen(
        userId= userId,
        navController = navController,
        dbHelper = dbHelper,
        isExamen = isExamen,
        onRegistrarRespuesta = onRegistrarRespuesta,
        onSiguiente = onSiguiente
    )

}