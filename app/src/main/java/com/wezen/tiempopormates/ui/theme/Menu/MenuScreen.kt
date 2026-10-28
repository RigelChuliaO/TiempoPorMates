package com.wezen.tiempopormates.ui.theme.Menu

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.wezen.tiempopormates.R
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.domain.model.usecase.formatearTiempo
import com.wezen.tiempopormates.data.entity.Module

private const val TAG = "MenuScreen"

@Composable
fun MenuScreen (
    dbHelper: DatabaseHelper,
    navController: NavController,
    userId: Long,
){

    // Crear el ViewModel con el factory
    val factory = MenuViewModelFactory(dbHelper)
    val viewModel: MenuViewModel = viewModel(factory = factory)

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    Log.d(TAG, "UserId Recibido: $userId")

    // Cargar usuario al iniciar
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
        viewModel.CargarConfigUsuario(userId)
    }
    Log.d(TAG, "User Viewmodel: ${uiState.user}")


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {

            if (uiState.user != null) {//Nos aseguramos que carga un usuario para pintar
                val user = uiState.user!!
                val configUser = uiState.configUser!!

                // Cabecera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¡Hola!, ${user.nombre} 👋",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontFamily = FontFamily(Font(R.font.letratiza)),
                    )

                    Row {
                        //Boton para acceder a las estadisticas del usuario
                        IconButton(
                            onClick = {navController.navigate("Estadisticas/${user.id}")},
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Text("📊", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        //Boton para acceder a la configuracion
                        IconButton(
                            onClick = { viewModel.openContraseñaDialog() },
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Text("⚙️", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        //Boton para retorcer al menu de Inicio
                        IconButton(
                            onClick = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Text("🚪", fontSize = 20.sp)
                        }
                    }
                }
                Text(
                    text = "Tiempo total: ${formatearTiempo(user.tiempo)}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.letratiza)),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grid de módulos
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.modules) { module ->
                        ModuleCard(
                            module = module,
                            onClick = { navController.navigate("${module.route}/${user.id}")}
                        )
                    }
                }
            }
        }
    }
    // --Dialogo para pedir la contraseña
    if (uiState.mostrarContraseñaDialogo && uiState.user != null) {
        PasswordAlertDialog(
            pulsaSalir = { viewModel.closeContraseñaDialog() },
            passwordCorrecto = {
                viewModel.closeContraseñaDialog()
                navController.navigate("config/${uiState.user!!.id}")
            },
            usuarioPassword = uiState.configUser!!.password,
            /*onValidatePassword = { password ->
                viewModel.validatePassword(password)
            },
            passwordError = uiState.error,
            onClearError = { viewModel.clearPasswordError() },
            isValidating = uiState.escontraseñaCorrecta*/

        )
    }
}
@Composable
//Esto Composable dibujara cada unos de los modulos con el icono y el nombre
fun ModuleCard(
    module: Module,
    onClick: () -> Unit
) {
    //Tarjeta en la que se encuentra cada Modulo
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .fillMaxWidth(0.9f)
            .clickable { onClick() },

        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        //Cada una de las etiquetas de los modulos
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF202020))
        ) {
            //Contenido de la tarjeta
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text( //Icono del Modulo
                    text = module.icon,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 20.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = module.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = module.color,

                        )
                    Text(
                        text = module.description,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                Icon(
                    Icons.Default.Add, // Cambiar por una flecha
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/***
 * Este Composable crea el mensaje para solicitar el Paswrd antes de acceder a la pagina de configuracion
 * @param pulsaSalir Accion al pulsar salir
 * @param passwordCorrecto Función a ejecutar si la contraseña es correcta
 * @param usuarioPassword La contraseña del usuario
 * Si el pasword es correcto abre configuracion
 */
@Composable
fun PasswordAlertDialog(
    pulsaSalir: () -> Unit,
    passwordCorrecto: () -> Unit,
    usuarioPassword: String?,

) {
    // Estado para guardar la contraseña que ingresa el usuario
    var passwordIntroducido by remember { mutableStateOf("") }

    // Estado para mostrar un mensaje de error si la contraseña es incorrecta
    var showPasswordError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = pulsaSalir,

        // --- TÍTULO ---
        title = { androidx.compose.material3.Text("Se requiere contraseña",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.letratiza))
        ) },

        // --- CONTENIDO ---
        text = {
            Column {
                OutlinedTextField(
                    value = passwordIntroducido,
                    onValueChange = {
                        passwordIntroducido = it
                        // Oculta el error mientras el usuario escribe
                        if (showPasswordError) showPasswordError = false
                    },
                    label = { androidx.compose.material3.Text("Contraseña") },
                    // Para ocultar la ccontraseña
                    visualTransformation = PasswordVisualTransformation()
                )
                //si la contraseña es incorrecta lanza un mensaje, No hay numero de intentos
                if (showPasswordError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Text(
                        "Contraseña incorrecta",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        // --- BOTONES --- del mensaje de Pasword
        //Boton Aceptar
        confirmButton = {
            Button(
                onClick = {
                    if (passwordIntroducido == usuarioPassword) {
                        passwordCorrecto() // Ejecuta la acción si es correcta
                        pulsaSalir() // Cierra el diálogo
                    } else {
                        showPasswordError = true // Muestra el mensaje de error
                        passwordIntroducido = "" // Opcional: limpia el campo
                    }
                }
            ) {
                androidx.compose.material3.Text("Aceptar",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.letratiza)))
            }
        },
        //Boton Salir
        dismissButton = {
            TextButton(
                onClick = pulsaSalir
            ) {
                androidx.compose.material3.Text("Salir",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.letratiza)))
            }
        }
    )
}
