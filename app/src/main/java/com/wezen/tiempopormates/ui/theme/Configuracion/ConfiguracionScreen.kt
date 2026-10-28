package com.wezen.tiempopormates.ui.theme.Configuracion


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wezen.tiempopormates.R
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.getModules
import com.wezen.tiempopormates.domain.model.usecase.formatearTiempo


private const val TAG = "ConfigScreen"

@Composable
fun ConfiguracionScreen(
    userId: Long,
    navController: NavController,
    dbHelper: DatabaseHelper
) {
    // Crear el ViewModel con el factory
    val factory = ConfigViewModelFactory(dbHelper)
    val viewModel: ConfigViewModel = viewModel(factory = factory)
    val contexto = LocalContext.current

    // Observar el estado
    val uiState by viewModel.uiState.collectAsState()

    // Estados para expandir/contraer secciones
    var sumaExpanded by remember { mutableStateOf(false) }
    var restaExpanded by remember { mutableStateOf(false) }
    var multiplicacionExpanded by remember { mutableStateOf(false) }
    var divisionExpanded by remember { mutableStateOf(false) }
    var tablasExpanded by remember { mutableStateOf(false) }
    var comprensionExpanded by remember { mutableStateOf(false) }
    var examenExpanded by remember { mutableStateOf(false) }
    var passwordExpanded by remember { mutableStateOf(false) }

    // Cargar usuario al iniciar
    LaunchedEffect(userId) {
        viewModel.CargarUsuario(userId)
        viewModel.CargarConfigUsuario(userId)
        //viewModel.loadUser(userId)
    }

    // Mostrar toast al guardar exitosamente
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(contexto, "Configuración actualizada", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }



    if (uiState.user != null) {
        val user = uiState.user!!
        val configUser = uiState.configUser!!
        Log.e(TAG, "ListModulosExamen de inicio: ${uiState.configUser!!.listadoModulosExamen}")


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF282424))
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("⬅️", fontSize = 24.sp)
                }
                Text(
                    text = "⚙️ Configuración",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar los cambios y salir de la página de configuración
            Button(
                onClick = {
                    //viewModel.actualizarConfiguracion(config)
                    viewModel.guardarConfiguracion()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.cambiosRealizados) Color(0xFF4CAF50) else Color(0xFF515951)
                ),
                enabled = !uiState.isSaving
            ) {
                Text(
                    "Guardar Cambios y salir",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontFamily = FontFamily(Font(R.font.letratiza))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección Suma
                item {
                    ExpandableConfigSection(
                        title = "➕ Sumas",
                        expanded = sumaExpanded,
                        onExpandChange = { sumaExpanded = it }
                    ) {
                        SliderConfig(
                            label = "Número de cifras",
                            value = configUser.numeroCifrasSuma,
                            range = 1f..4f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(numeroCifrasSuma = it.toInt())
                            )}
                        )
                        SliderConfig(
                            label = "Tiempo extra (segundos)",
                            value = configUser.tiempoExtraSuma,
                            range = 0f..30f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(tiempoExtraSuma = it.toInt()))
                            }
                        )
                        SwitchConfig(
                            label = "Con llevadas",
                            checked = configUser.llevadasSuma,
                            onCheckedChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(llevadasSuma = it))
                            }
                        )
                    }
                }

                // Sección Resta
                item {
                    ExpandableConfigSection(
                        title = "➖ Restas",
                        expanded = restaExpanded,
                        onExpandChange = { restaExpanded = it }
                    ) {
                        SliderConfig(
                            label = "Número de cifras",
                            value = configUser.numeroCifrasResta,
                            range = 1f..4f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(numeroCifrasResta = it.toInt()))
                            }
                        )
                        SliderConfig(
                            label = "Tiempo extra (segundos)",
                            value = configUser.tiempoExtraResta,
                            range = 0f..30f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(tiempoExtraResta = it.toInt()))
                            }
                        )
                        SwitchConfig(
                            label = "Con llevadas",
                            checked = configUser.llevadasResta,
                            onCheckedChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(llevadasResta = it))
                            }
                        )
                    }
                }

                // Sección Multiplicación
                item {
                    ExpandableConfigSection(
                        title = "✖️ Multiplicaciones",
                        expanded = multiplicacionExpanded,
                        onExpandChange = { multiplicacionExpanded = it }
                    ) {
                        SliderConfig(
                            label = "Cifras Multiplicando",
                            value = configUser.numeroCifrasMultiplicando,
                            range = 2f..4f,
                            onValueChange = {
                                // Como protección el multiplicador nunca podra ser mayor que el Multiplicando
                                val newValue = it.toInt()
                                val newMultiplicador = if (newValue < configUser.numeroCifrasMultiplicador) {
                                    newValue
                                } else {
                                    configUser.numeroCifrasMultiplicador
                                }
                                viewModel.actualizarConfiguracion(configUser.copy(
                                    numeroCifrasMultiplicando = newValue,
                                    numeroCifrasMultiplicador = newMultiplicador)
                                )

                            }
                        )
                        SliderConfig(
                            label = "Cifras Multiplicador",
                            value = configUser.numeroCifrasMultiplicador,
                            range = 1f..3f,
                            onValueChange = {
                                val newValue = it.toInt()
                                val newMultiplicando = if (newValue > configUser.numeroCifrasMultiplicando) {
                                    newValue
                                } else {
                                    configUser.numeroCifrasMultiplicando
                                }
                                viewModel.actualizarConfiguracion(configUser.copy(
                                    numeroCifrasMultiplicando = newMultiplicando,
                                    numeroCifrasMultiplicador = newValue)
                                )
                            }
                        )
                        SliderConfig(
                            label = "Tiempo extra (segundos)",
                            value = configUser.tiempoExtraMultiplicacion,
                            range = 1f..30f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(tiempoExtraMultiplicacion = it.toInt()))
                            }
                        )
                    }
                }

                // Sección División
                item {
                    ExpandableConfigSection(
                        title = "➗ Divisiones",
                        expanded = divisionExpanded,
                        onExpandChange = { divisionExpanded = it }
                    ) {
                        SliderConfig(
                            label = "Número de cifras Dividendo",
                            value = configUser.numeroCifrasDividendo,
                            range = 1f..4f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(numeroCifrasDividendo = it.toInt()))
                            }
                        )
                        SliderConfig(
                            label = "Número de cifras Divisor",
                            value = configUser.numeroCifrasDivisor,
                            range = 1f..4f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(numeroCifrasDivisor = it.toInt()))
                            }
                        )
                        SliderConfig(
                            label = "Tiempo extra (segundos)",
                            value = configUser.tiempoExtraDivision,
                            range = 0f..30f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(tiempoExtraDivision = it.toInt())) }
                        )
                        SwitchConfig(
                            label = "Exactas",
                            checked = configUser.soloExactasDivision,
                            onCheckedChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(soloExactasDivision = it))
                            }
                        )
                    }
                }

                // Sección Tablas de multiplicar
                item {
                    ExpandableConfigSection(
                        title = "📋 Tablas Multiplicar",
                        expanded = tablasExpanded,
                        onExpandChange = { tablasExpanded = it }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Selecciona las tablas:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = Color.White
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                var indexTabla = 1
                                repeat(2) { fila ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        repeat(4) { columna ->
                                            indexTabla += 1
                                            if (indexTabla <= 9) {
                                                val tablaNum = indexTabla
                                                val tablasSeleccionadas = configUser.tablasMultiplicar.split(",").mapNotNull { it.toIntOrNull() }
                                                val estSeleccionada = tablasSeleccionadas.contains(tablaNum)

                                                Button(
                                                    onClick = {
                                                        val nuevasTablas = if (estSeleccionada) {
                                                            tablasSeleccionadas
                                                                .filter { it != tablaNum }
                                                                .joinToString(",")
                                                        } else {
                                                            (tablasSeleccionadas + tablaNum)
                                                                .sorted()
                                                                .joinToString(",")
                                                        }

                                                        viewModel.actualizarConfiguracion(
                                                            configUser.copy(
                                                                tablasMultiplicar = nuevasTablas.ifEmpty { "2,3,4,5,6,7,8,9" }
                                                            )
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (estSeleccionada)
                                                            MaterialTheme.colorScheme.primary
                                                        else
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (estSeleccionada)
                                                            MaterialTheme.colorScheme.onPrimary
                                                        else
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text(tablaNum.toString())
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Tablas seleccionadas: ${configUser.tablasMultiplicar}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .alpha(0.7f),
                                color = Color.White
                            )
                        }
                        SliderConfig(
                            label = "Tiempo extra (segundos)",
                            value = configUser.tiempoExtratablasMultiplicar,
                            range = 0f..30f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(tiempoExtratablasMultiplicar = it.toInt()))
                            }
                        )
                    }
                }

                // Sección Comprensión
                item {
                    ExpandableConfigSection(
                        title = "🧠 Comprensión",
                        expanded = comprensionExpanded,
                        onExpandChange = { comprensionExpanded = it }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Selecciona los módulos:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = Color.White
                            )

                            val operaciones = listOf("Suma", "Resta", "Multiplicación", "División")
                            val operacionesValores = listOf("suma", "resta", "multiplicar", "dividir")

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(2) { fila ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        repeat(3) { columna ->
                                            val index = fila * 3 + columna
                                            if (index < operaciones.size) {
                                                val moduloNombre = operaciones[index]
                                                val moduloValor = operacionesValores[index]
                                                val ejerciciosComprension = configUser.ejerciciosComprension.split(",").map { it.trim() }
                                                val estSeleccionado = ejerciciosComprension.contains(moduloValor)

                                                Button(
                                                    onClick = {
                                                        val nuevos  = if (estSeleccionado) {
                                                            ejerciciosComprension
                                                                .filter { it != moduloValor }
                                                                .joinToString(",")
                                                        } else {
                                                            (ejerciciosComprension + moduloValor)
                                                                .joinToString(",")
                                                        }
                                                        viewModel.actualizarConfiguracion(configUser.copy(ejerciciosComprension = nuevos.ifEmpty { "suma,resta,multiplicar,dividir" }))

                                                    },
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .weight(1f)
                                                        .height(45.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (estSeleccionado)
                                                            MaterialTheme.colorScheme.primary
                                                        else
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (estSeleccionado)
                                                            MaterialTheme.colorScheme.onPrimary
                                                        else
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text(moduloNombre, fontSize = 12.sp)
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Módulos seleccionados: ${configUser.ejerciciosComprension}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .alpha(0.7f),
                                color = Color.White
                            )
                        }

                        SliderConfig(
                            label = "Tiempo extra (segundos)",
                            value = configUser.tiempoExtraComprension,
                            range = 0f..30f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(tiempoExtraComprension = it.toInt()))
                            }
                        )
                    }
                }

                // Sección Examen
                item {
                    ExpandableConfigSection(
                        title = "📝 Exámenes",
                        expanded = examenExpanded,
                        onExpandChange = { examenExpanded = it }
                    ) {

                        val modulos = getModules().take(6)
                        val modulosSeleccionados  = configUser.listadoModulosExamen.split(",").map { it.trim().lowercase() }

                        repeat((modulos.size + 2) / 3) { fila ->  // Calcular filas dinámicamente
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(3) { columna ->
                                    val index = fila * 3 + columna
                                    if (index < modulos.size) {
                                        val modulo = modulos[index]
                                        val estSeleccionado = modulosSeleccionados .contains(modulo.route.lowercase())

                                        Button(
                                            onClick = {
                                                val nuevos = if (estSeleccionado) {
                                                    modulosSeleccionados
                                                        .filter { it != modulo.route }
                                                        .joinToString(",")
                                                } else {
                                                    (modulosSeleccionados  + modulo.route)
                                                        .joinToString(",")
                                                }
                                                viewModel.actualizarConfiguracion(
                                                    configUser.copy(
                                                        listadoModulosExamen = nuevos.ifEmpty {
                                                            "suma,resta,multiplicar,dividir,tablas,comprension"
                                                        }
                                                    )
                                                )
                                                Log.e(TAG, "ListModulosExamen: ${modulosSeleccionados}")
                                            },
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .weight(1f)
                                                .height(45.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (estSeleccionado)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = if (estSeleccionado)
                                                    MaterialTheme.colorScheme.onPrimary
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("${modulo.nombre}", fontSize = 12.sp)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        SliderConfig(
                            label = "Tiempo extra (segundos)",
                            value = configUser.tiempoExtraExamen,
                            range = 0f..30f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(tiempoExtraExamen = it.toInt()))
                            }
                        )
                        SliderConfig(
                            label = "Nº Preguntas",
                            value = configUser.numeroPreguntasExamen,
                            range = 1f..12f,
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(numeroPreguntasExamen = it.toInt()))
                            }
                        )
                    }
                }

                // Sección Password
                item {
                    ExpandableConfigSection(
                        title = "🔒 Password",
                        expanded = passwordExpanded,
                        onExpandChange = { passwordExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = configUser.password ?: "",
                            onValueChange = {
                                viewModel.actualizarConfiguracion(configUser.copy(password = it))
                            },
                            label = { Text("Contraseña", color = Color.White) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.White,
                                focusedBorderColor = Color.White,
                                cursorColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            )
                        )
                    }
                }

                // Sección Tiempo acumulado actual del usuario
                item {
                    Text(
                        text = "Tiempo total: ${formatearTiempo(user.tiempo)}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Botón para reiniciar el tiempo ganado
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.abrirDialogoResetearTiempo() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
                    ) {
                        Text("Reiniciar contador de Tiempo", color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        // Diálogo de confirmación para resetear tiempo
        if (uiState.showDeleteTimeDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.cerrarDialogoResetearTiempo() },
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
                    Text("Si continúa, el contador de tiempo se pondrá a 0.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetearTiempo()
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cerrarDialogoResetearTiempo() }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// ==================== COMPONENTES ====================

@Composable
fun SliderConfig(
    label: String,
    value: Int,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 16.sp, color = Color.White)
            Text(
                text = value.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = range,
            steps = (range.endInclusive - range.start - 1).toInt(),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SwitchConfig(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.White)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ExpandableConfigSection(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF202020)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Cabecera clickable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandChange(!expanded) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = Color.White
                )
            }

            // Contenido expandible
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp), color = Color.Gray)
                    content()
                }
            }
        }
    }
}