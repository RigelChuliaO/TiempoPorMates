package com.wezen.tiempopormates.ui.theme.Inicio

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wezen.tiempopormates.R
import com.wezen.tiempopormates.Util.FormatearTiempo
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.domain.model.usecase.DeleteUserCase

private const val TAG = "InicioScreen"

@Composable
fun InicioScreen(
    dbHelper: DatabaseHelper,
    navController: NavController,
) {

    // Crear el ViewModel con el factory
    val factory = InicioViewModelFactory(dbHelper)
    val viewModel: InicioViewModel = viewModel(factory = factory)
    var showWarning by remember { mutableStateOf(false) }
    var contraseñaUsuario by remember { mutableStateOf("") }
    var showErrorcontraseñaBorrar by remember { mutableStateOf(false) }

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título principal
            Image(
                painter = painterResource(id = R.drawable.tpmiconov31), // Reemplaza con tu recurso
                contentDescription = "Logo TiempoPorMates",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "¡¿Que, le damos un repaso a las Mates!",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Card con usuarios
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF202020) // Un gris muy oscuro para el card
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Selecciona tu perfil:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color(0xFFEFECCB).copy(alpha = 0.8f)
                    )

                    // Lista de usuarios existentes
                    LazyColumn {
                        Log.d(TAG, "Lista Users InicioVieModel ${uiState.users}")
                        items(uiState.users.size) { index ->
                            val user = uiState.users[index]
                            UserCard(
                                user = user,
                                onClick = { navController.navigate("menu/${user.id}")  },
                                onBorrarClick = {
                                    viewModel.CargarConfigUsuario(user.id)
                                    viewModel.openDeleteUserDialog(user.id)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    // Botón crear nuevo usuario
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openCreateUserDialog() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4F4A4A)
                        )

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp),

                                )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Crear nuevo usuario",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Dialog para crear usuario
        if (uiState.showCreateUserDialog) {

            // Lista de iconos disponibles
            val availableIcons = listOf(
                "😀", "😎", "🤓", "😺", "🦊", "🐻", "🐼", "🦁",
                "🎀", "🐯", "🐨", "🐸", "🦄", "🐲", "🚀", "⚽", "🎮"
            )
            // Estado para el icono seleccionado (por defecto el primero)
            var avatares by remember { mutableStateOf(availableIcons[0]) }
            var newUserName by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { viewModel.closeCreateUserDialog() },
                title = { Text("Nuevo Usuario") },
                text = {
                    Column {
                        Text("Escribe tu nombre:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newUserName,
                            onValueChange = { newUserName = it },
                            placeholder = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Selecciona tu avatar:")
                        Spacer(modifier = Modifier.height(8.dp))

                        // Grid de iconos
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(150.dp)
                        ) {
                            items(availableIcons) { icon ->
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (icon == avatares)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { avatares = icon }
                                        .border(
                                            width = if (icon == avatares) 2.dp else 0.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = icon,
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = newUserName.isNotBlank(),
                        onClick = {
                            showWarning = true
                            //viewModel.createNewUser(newUserName, avatares)
                        }
                    ) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeCreateUserDialog() }) {
                        Text("Cancelar")
                    }
                }
            )

            if (showWarning) {
                MensajeAdvertenciaContraseña(
                    onAccept = {
                        showWarning = false
                        viewModel.createNewUser(
                            nombre = newUserName,
                            avatar = avatares
                        )
                    }
                )
            }
        }


        if (uiState.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { /* no cerrar tocando fuera */ },

                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(40.dp)
                    )
                },

                title = {
                    Text(
                        text = "Advertencia",
                        style = MaterialTheme.typography.titleLarge
                    )
                },

                text = {
                    Column {
                        Text("Para borrar el usuario y sus datos, introduce la contraseña:")

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = contraseñaUsuario ,//.configUser!!.password.toString(),
                            onValueChange = {
                                contraseñaUsuario = it
                                showErrorcontraseñaBorrar = false
                            },
                            label = { Text("Contraseña") },
                            isError = showErrorcontraseñaBorrar,
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            )
                        )

                        if (showErrorcontraseñaBorrar) {
                            Text(
                                text = "Contraseña incorrecta",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },

                confirmButton = {
                    TextButton(
                        enabled = contraseñaUsuario.isNotBlank(),
                        onClick = {
                            if (contraseñaUsuario==uiState.configUser!!.password) {
                                viewModel.DeleteUser()
                                viewModel.closeDeleteUserDialog()
                                contraseñaUsuario = ""
                            } else {
                                showErrorcontraseñaBorrar = true
                            }
                        }
                    ) {
                        Text("Aceptar")
                    }
                },

                dismissButton = {
                    TextButton(onClick = {
                        contraseñaUsuario = ""
                        showErrorcontraseñaBorrar = false
                        viewModel.closeDeleteUserDialog()
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun UserCard(
    user: User = User(1, "Juan", "🐸"),
    onClick: () -> Unit = {},
    onBorrarClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF312F2F) // Un gris muy oscuro para el card
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatar del usuario
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,

                ) {
                Text(
                    text = user.avatar ?: "",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = user.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color =Color(0xFFEFECCB).copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = ": "+FormatearTiempo(user.tiempo),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color =Color(0xFFEFECCB).copy(alpha = 0.8f)
            )

            // Icono de Basura para poder borrar al usuario
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar usuario",
                tint = Color.Red.copy(alpha = 0.9f), // Un color visible y de advertencia
                modifier = Modifier
                    .size(32.dp)
                    .clickable {

                        //Abrimos el mensaje para borrar el usuario
                        onBorrarClick()

                    }
                    .padding(4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MensajeAdvertenciaContraseña(
    onAccept: () -> Unit= {})
{
    AlertDialog(
        onDismissRequest = { /* evitar que se cierre fuera */ },
        title = { Text("Aviso de seguridad") },
        text = { Text("La contraseña inicial del usuario es '1234'. Deberá cambiarla lo antes posible.") },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text("Aceptar")
            }
        }
    )
}