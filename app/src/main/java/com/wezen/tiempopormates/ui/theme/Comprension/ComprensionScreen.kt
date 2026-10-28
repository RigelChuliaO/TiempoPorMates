package com.wezen.tiempopormates.ui.theme.Comprension

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.wezen.tiempopormates.R
import coil.decode.ImageDecoderDecoder
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.Gemini.GeminiServiceImpl
import com.wezen.tiempopormates.ui.theme.Suma.SumaScreen
import com.wezen.tiempopormates.ui.theme.theme.BotonAtras
import com.wezen.tiempopormates.ui.theme.theme.BotonSiguiente
import com.wezen.tiempopormates.ui.theme.theme.BrilloTexto

private const val TAG = "ComprensionScreen"

@Composable
fun ComprensionScreen (
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = false,
    onSiguiente: () -> Unit,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null

){
    // Crear el ViewModel con el factory
    val factory = ComprensionViewModelFactory(dbHelper)
    val viewModel: ComprensionViewModel = viewModel(factory = factory)

    Log.d(TAG, "User Comprension: ${userId}")

    val contexto = LocalContext.current

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    // Cargar usuario al iniciar
    LaunchedEffect(userId) {
        viewModel.CargarUsuario(userId)
        viewModel.CargarConfigUsuario(userId)
        if(uiState.cambiarHistoria ==true) {
            viewModel.leerHistoria()
        }
    }

    Log.d(TAG, "historia: ${uiState.historiaEnPantalla}")
    Log.d(TAG, "User Viewmodel: ${uiState.user}")

    if (uiState.user != null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .padding(start = 16.dp)
            )
        {
            // Quitamos el boton de atras si es examen
            if (isExamen==false) {
                Spacer(modifier = Modifier.height(24.dp))
                BotonAtras(
                    onClick = {
                        navController.popBackStack()
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Titlo del modulo
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
                    text = "🧠 Comprension",
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val context = LocalContext.current

            //Muestra el Icono de pensando o de IA segun corresponda
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .size(100.dp),

                    ) {
                    Log.d(TAG, "Pensando...")

                    // Utilizamos el IamgeRequest para cargar la imagen desde el asset
                    val imageRequest = ImageRequest.Builder(context)
                        .data("file:///android_asset/pensandoia.gif")
                        .decoderFactory(ImageDecoderDecoder.Factory())
                        .allowHardware(false)
                        .build()

                    Image(
                        painter = rememberAsyncImagePainter(imageRequest),
                        contentDescription = "Pensando...",
                        modifier = Modifier
                            .size(200.dp)
                    )

                }
                Text(
                    text = "Pensando...",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEFECCB).copy(alpha = 0.8f)
                )
            } else if (uiState.historiaEnPantalla != null){

                Log.d(TAG, "No estoy pensado")

                //Texto donde aparece la historia creada por la IA
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    textAlign = TextAlign.Justify,
                    fontSize = 20.sp,
                    text =  uiState.historiaEnPantalla?.historia ?: "Error cargando historia",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.letratiza))
                )


            }

            Spacer(modifier = Modifier.height(height = 16.dp))


            if (uiState.comprensionCorregida==false) {
                //Botones para la respuesta de la operacion.
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column() {
                        Miboton("➕ Sumar", onClick = {
                            viewModel.corregirComprension("suma")
                        })
                        Miboton("✖️ Multiplicar", onClick = {
                            viewModel.corregirComprension("multiplicar")
                        })
                    }
                    Column() {
                        Miboton("➖ Restar", onClick = {
                            viewModel.corregirComprension("resta")
                        })
                        Miboton("➗ Dividir", onClick = {
                            viewModel.corregirComprension("dividir")
                        })
                    }

                }

            } else {
                // Botones cuando el ejercicio esta corregido
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = navController::popBackStack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
                    ) {
                        Text(
                            "Atrás",
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    BotonSiguiente(
                        text = if (isExamen) "Siguiente Ejercicio" else "Otra Historia!",
                        onClick = {
                            if (isExamen) {
                                // En examen: registrar respuesta y pasar a siguiente
                                Log.d(
                                    TAG,
                                    "Registrando respuesta en examen: ${uiState.comprensionCorrecta}"
                                )
                                onRegistrarRespuesta?.invoke(uiState.comprensionCorrecta ?: false)
                                onSiguiente()
                            } else {
                                // Fuera de examen: generar nueva suma
                                viewModel.leerHistoria()
                            }
                        }
                    )
                }
                // Para que informe al usuario de su resultado
                val user = uiState.user!!
                if (uiState.comprensionCorrecta == true) {
                    Toast.makeText(
                        contexto,
                        "¡Muy bien! ${user.nombre} ✓",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (uiState.comprensionCorrecta  == false) {
                    val resultado = uiState.historiaEnPantalla!!.respuesta
                    Toast.makeText(
                        contexto,
                        "Se trataba de una $resultado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }
}
//Boton Siguiente
@Composable
fun BotonSiguiente(
    text: String,//Texto que aparece sobre el boton
    onClick: () -> Unit, //Lo que hara cuando lo pulse
    modifier: Modifier = Modifier //Por si se le tiene que pedir alguna modificacion al boton estandar
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF868C80),
            contentColor = Color(0xFF868C80)
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.letratiza))
        )
    }
}
@Composable
fun Miboton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)),
        modifier = Modifier
            .padding(8.dp)
            .height(40.dp)
    ) {
        Text(text= text,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily(Font(R.font.letratiza)),
            color = Color.White
        )
    }
}

//Este es el Composable que llamaremos desde el Examen
@Composable
fun ComprensionScreenExamen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper,
    isExamen: Boolean = true,
    onRegistrarRespuesta: ((esCorrecto: Boolean) -> Unit)? = null,
    onSiguiente: () -> Unit = {}
) {
    ComprensionScreen(
        userId= userId,
        navController = navController,
        dbHelper = dbHelper,
        isExamen = isExamen,
        onRegistrarRespuesta = onRegistrarRespuesta,
        onSiguiente = onSiguiente
    )

}