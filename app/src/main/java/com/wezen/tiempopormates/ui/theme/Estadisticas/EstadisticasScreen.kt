package com.wezen.tiempopormates.ui.theme.Estadisticas


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.wezen.tiempopormates.R
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.DatosUsuarioModulo
import com.wezen.tiempopormates.data.entity.EstadisticaTabla
import com.wezen.tiempopormates.data.entity.Examen
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.domain.model.usecase.formatearTiempo
import com.wezen.tiempopormates.domain.model.usecase.obtenerNota
import com.wezen.tiempopormates.ui.theme.theme.BotonAtras
import com.wezen.tiempopormates.ui.theme.theme.BrilloTexto

private const val TAG = "EstadisticasScreen"

@Composable
fun EstadisticasScreen(
    userId: Long,
    dbHelper: DatabaseHelper,
    navController: NavController
) {
    // Crear el ViewModel con el factory
    val factory = EstadisticasViewModelFactory(dbHelper)
    val viewModel: EstadisticasViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    val expandedSection by viewModel.expandedSection.collectAsState()
    val selectedTablaFilter by viewModel.selectedTablaFilter.collectAsState()
    //val datosModulosUser by viewModel.datosModulo.collectAsState()


    LaunchedEffect(userId) {
        viewModel.cargarEstadisticasTablas(userId)
        viewModel.CargarUsuario(userId)
        viewModel.CargarConfigUsuario(userId)
        viewModel.CargarDatosModulos(userId)
        viewModel.CargarExamenes(userId)

    }

    Log.d(TAG, "User Viewmodel: ${uiState.user}")

    if (uiState.user != null) {

        val user = uiState.user!!
        val datosModulosUser = uiState.datosUsuarioModulo!!

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .paint(
                    painterResource(id = R.drawable.fondo),
                    contentScale = ContentScale.FillBounds
                )
        ) {

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Boton Atras, en caso de ser un Examen no se activa
                BotonAtras(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {
                        navController.popBackStack()
                    },
                )
            }

            // Título de la Pagina de Estadisticas
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                ) {
                    Column (
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        BrilloTexto(
                            text = "📊 Estadisticas",
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        BrilloTexto(
                            text = user.nombre,
                            color = Color(0xCDD2C339),

                        )

                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                androidx.wear.compose.material.Text(
                    text = "Tiempo total: ${formatearTiempo(user.tiempo)}",
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.letratiza))
                )

            }
            // Seccion General de las estadisticas(Datos de todas los modulos juntos)
            item {
                SeccionDesplegable(
                    titulo = "General",
                    icono = "📊",
                    expandida = expandedSection == "General",
                    onToggle = {
                        viewModel.toggleSection("General")
                    }
                ) {
                    EstadisticasGenerales(
                        user,
                        datosModulosUser,
                        uiState.estadisticasTablas
                    )
                }
            }

            // Sumas
            item {
                SeccionDesplegable(
                    titulo = "Sumas",
                    icono = "➕",
                    expandida = expandedSection == "Sumas",
                    onToggle = {
                        viewModel.toggleSection("Sumas")
                    }
                ) {
                    EstadisticasModulo(
                        ejerciciosRealizados = datosModulosUser.sumaRealizados.toString(),
                        fallos = datosModulosUser.sumaFallos.toString(),
                        porcetanjeAciertos = datosModulosUser.porcentajeSuma,
                    )
                }
            }

            // Restas
            item {
                SeccionDesplegable(
                    titulo = "Restas",
                    icono = "➖",
                    expandida = expandedSection == "Restas",
                    onToggle = {
                        viewModel.toggleSection("Restas")
                    }
                ) {
                    EstadisticasModulo(
                        ejerciciosRealizados = datosModulosUser.restaRealizados.toString(),
                        fallos = datosModulosUser.restaFallos.toString(),
                        porcetanjeAciertos = datosModulosUser.porcentajeResta,
                    )
                }
            }

            // Multiplicaciones
            item {
                SeccionDesplegable(
                    titulo = "Multiplicaciones",
                    icono = "✖️",
                    expandida = expandedSection == "Multiplicaciones",
                    onToggle = {
                        viewModel.toggleSection("Multiplicaciones")
                    }
                ) {
                    EstadisticasModulo(
                        ejerciciosRealizados = datosModulosUser.multiplicacionRealizados.toString(),
                        fallos = datosModulosUser.multiplicacionFallos.toString(),
                        porcetanjeAciertos = datosModulosUser.porcentajeMultiplicacion,
                    )
                }
            }

            // Divisiones
            item {
                SeccionDesplegable(
                    titulo = "Divisiones",
                    icono = "➗",
                    expandida = expandedSection == "Divisiones",
                    onToggle = {
                        viewModel.toggleSection("Divisiones")
                    }
                ) {
                    EstadisticasModulo(
                        ejerciciosRealizados = datosModulosUser.divisionRealizados.toString(),
                        fallos = datosModulosUser.divisionFallos.toString(),
                        porcetanjeAciertos = datosModulosUser.porcentajeDivision,
                    )
                }
            }

            // Tablas
            item {
                SeccionDesplegable(
                    titulo = "Tablas de Multiplicar",
                    icono = "🔢",
                    expandida = expandedSection == "Tablas",
                    onToggle = {
                        viewModel.toggleSection("Tablas")
                    }
                ) {
                    EstadisticasTablas(
                        uiState = uiState,
                        selectedTabla = selectedTablaFilter,
                        onTablaSelected = { viewModel.selectTabla(it) }
                    )
                }
            }

            // Comprensión
            item {
                SeccionDesplegable(
                    titulo = "Comprensión",
                    icono = "📖",
                    expandida = expandedSection == "Comprension",
                    onToggle = {
                        viewModel.toggleSection("Comprension")
                    }
                ) {
                    EstadisticasModulo(
                        ejerciciosRealizados = datosModulosUser.comprensionRealizados.toString(),
                        fallos = datosModulosUser.comprensionFallos.toString(),
                        porcetanjeAciertos = datosModulosUser.porcentajeComprension,
                    )
                }
            }
            // Exámenes
            item {
                SeccionDesplegable(
                    titulo = "Exámenes",
                    icono = "📝",
                    expandida = expandedSection == "Examenes",
                    onToggle = {
                        viewModel.toggleSection("Examenes")
                    }
                ) {
                    EstadisticasExamenes(uiState.examenes)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SeccionDesplegable(
    titulo: String,
    icono: String,
    expandida: Boolean,
    onToggle: () -> Unit,
    contenido: @Composable () -> Unit
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xD0484242))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = icono,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (expandida) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandida) "Contraer" else "Expandir",
                    tint = Color(0xE6E03063)
                )
            }
            AnimatedVisibility(visible = expandida) {
                Column {
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    contenido()
                }
            }
        }
    }
}

@Composable
fun EstadisticasGenerales(
    user: User,
    datosModulos: DatosUsuarioModulo,
    estadisticas: Map<Int, EstadisticaTabla>
) {
    Column(modifier = Modifier.padding(16.dp)) {

        EstadisticaItem("⏱️ Tiempo acumulado", formatearTiempo(user.tiempo))
        EstadisticaItem("📝 Ejercicios totales", datosModulos.totalRealizados.toString())
        EstadisticaItem("❌ Fallos totales", datosModulos.totalFallos.toString())
        EstadisticaItem("📊 Porcentaje de aciertos", String.format("%.1f%%", datosModulos.porcentajeTotal))
        EstadisticaItem("🏆 Mejor área", datosModulos.mejorArea)
        EstadisticaItem("📈 Área a mejorar", datosModulos.peorArea)
    }
}

@Composable
fun EstadisticasModulo(
    ejerciciosRealizados: String = "0",
    fallos: String = "0",
    porcetanjeAciertos: Double = 0.0
) {
    //val porcetanjeAciertos=porcentaje(ejerciciosRealizados.toInt(),fallos.toInt())

    Log.e(TAG, "porcetanjeAciertos: $porcetanjeAciertos")
    Log.e(TAG, "Realizados: $ejerciciosRealizados")
    Log.e(TAG, "Fallos: ${fallos}")
    //Log.e(TAG, "porcentajeAciertos MIO: ${((ejerciciosRealizados.toDouble()-fallos.toDouble())/(ejerciciosRealizados.toDouble()+fallos.toDouble())*100)}")



    val barColor = when {
        porcetanjeAciertos >= 80 -> Color(0xFF4CAF50)  // Verde
        porcetanjeAciertos >= 50 -> Color(0xFFFFC107)  // Amarillo
        else -> Color(0xFFF44336)              // Rojo
    }
    val mensajeEstado = when {
        porcetanjeAciertos >= 80 -> "¡Excelente! 🎉"
        porcetanjeAciertos >= 50 -> "¡Buen trabajo! 💪"
        else -> "Necesitas mejorar 📚"
    }
    Column(modifier = Modifier.padding(16.dp)) {

        EstadisticaItem("📝 Ejercicios realizados", ejerciciosRealizados)
        EstadisticaItem("❌ Fallos", fallos)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "📊 Resultado",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp),
            fontFamily = FontFamily(Font(R.font.letratiza))
        )

        // Contenedor de la barra con porcentaje
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Barra de progreso
            LinearProgressIndicator(
                progress = { (porcetanjeAciertos / 100).toFloat() },
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = barColor,
                trackColor = Color(0xD0484242)
            )

            // Porcentaje
            Text(
                text = obtenerNota(porcetanjeAciertos),//String.format("%.1f%%", porcetanjeAciertos),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = barColor,
                modifier = Modifier.padding(start = 12.dp),
                textAlign = TextAlign.Center
            )
        }
        // Mensaje de estado
        Text(
            text = mensajeEstado,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = barColor,
            modifier = Modifier.padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun EstadisticasTablas(
    uiState: EstadisticasUiState,
    selectedTabla: Int?,
    onTablaSelected: (Int?) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        val (totalrealizadas, totalfallos, porcentaje) = calcularTotalesEstadisticasTablas(uiState.estadisticasTablas)

        EstadisticaItem("📝 Ejercicios totales", uiState.ejerciciosTotalesTablas.toString())
        EstadisticaItem("❌ Fallos totales", uiState.fallosTotalesTablas.toString())
        EstadisticaItem("📊 Porcentaje de aciertos", String.format("%.1f%%", uiState.porcentajeTotalTablas))
        EstadisticaItem("📈 Tabla a mejorar", uiState.peorTabla)
        EstadisticaItem("🏆 Mejor tabla", uiState.mejorTabla)

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selecciona una tabla:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {// Primera fila de botones de las tablas del 2 al 4

            (2..5).forEach { numero ->
                FilterChip(
                    selected = selectedTabla == numero,
                    onClick = {
                        onTablaSelected(if (selectedTabla == numero) null else numero)
                    },
                    label = { Text(numero.toString()) },
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
        Row( //Segunda fila de botos del 5 al 9
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (6..9).forEach { numero ->
                FilterChip(
                    selected = selectedTabla == numero,
                    onClick = {
                        onTablaSelected(if (selectedTabla == numero) null else numero)
                    },
                    label = { Text(numero.toString()) },
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }

        if (selectedTabla != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xD0484242)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tabla del $selectedTabla",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDFD8F1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val estadistica = uiState.estadisticasTablas[selectedTabla]
                    EstadisticaItem("📝 Ejercicios realizados", estadistica?.realizados.toString())
                    EstadisticaItem("❌ Fallos", estadistica?.fallos.toString())
                    EstadisticaItem("📊 Resultado", obtenerNota(estadistica?.porcenrajeAciertos ?: 0.0))
                }
            }
        }
    }
}
/**
 *para representar las valores de las tablas y que quede el codigo mas limpio en la parte superior
 */
@Composable
fun EstadisticaItem(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(  //Etiqueta
            text = label,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.letratiza))

        )
        Text(   //Valor
            text = valor,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.letratiza))
        )
    }
}
/**
 * Funcion para el calculo de los totales de las estadisiticas de las tablas de multiplicar.
 * @param estadisticas Mapa de estadisticas de las tablas de multiplicar.Todos los datos de todas las tablas que tiene el usuario guardados en la BBDD
 * @return Totales del usuario solo para las tablas del usuario.
 */
fun calcularTotalesEstadisticasTablas(estadisticas: Map<Int, EstadisticaTabla>): Triple<Int, Int,Double> {
    var totalRealizados = 0
    var totalFallos = 0

    //caluclo los valores totales de todas las tablas
    for (estadistica in estadisticas.values) {
        totalRealizados += estadistica.realizados
        totalFallos += estadistica.fallos
    }

    //Calculo del porcentaje de Aciertos de todos las tablas
    val totalAciertos = totalRealizados - totalFallos
    val porcentajeAciertos = if (totalRealizados > 0)
        (totalAciertos.toDouble() / totalRealizados.toDouble()) * 100
    else
        0.0

    Log.e(TAG, "totalRealizados: $totalRealizados,totalFallos: $totalFallos,porcentajeAciertos: $porcentajeAciertos")

    return Triple(totalRealizados,totalFallos,porcentajeAciertos)
}

/**
 * Funcion para obtener las mejor y las peores tablas de multiplicar.
 * @param estadisticas Mapa de estadisticas de las tablas de multiplicar.Todos los datos de todas las tablas que tiene el usuario guardados en la BBDD
 * @return Mejor y Peor tabla de multiplicar del usuario en formato String
 */
fun CalculoMejorYPeorTabla(estadisticas: Map<Int, EstadisticaTabla>): Pair<String, String>{

    val estadisticasConEjercicios = estadisticas.filterValues { it.realizados > 0 }//Para que no de como mejores aquellas que son 0
    val maxPorcentaje = estadisticas.maxOfOrNull { it.value.porcenrajeAciertos } ?: 0.0

    // Buscar mejor y peor tabla según el porcentaje individual

    val minPorcentaje = estadisticas.minOfOrNull { it.value.porcenrajeAciertos } ?: 0.0

// Obtener listas de tablas que coinciden con esos valores
    val mejores = estadisticasConEjercicios
        .filterValues { it.porcenrajeAciertos == maxPorcentaje }
        .keys.sorted()

    val peores = estadisticas
        .filterValues { it.porcenrajeAciertos == minPorcentaje }
        .keys.sorted()

    // Construir los textos
    val mejoresTexto = if (mejores.size == 1)
        "${mejores.first()}"
    else
        "${mejores.joinToString("-")}"

    val peoresTexto = if (peores.size == 1)
        "${peores.first()}"
    else
        "${peores.joinToString("-")}"

    return Pair(mejoresTexto,peoresTexto)

}

@Composable
fun EstadisticasExamenes(
    examenes: List<Examen>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (examenes.isEmpty()) {
            Text(
                text = "No hay exámenes realizados",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            Text(
                text = "Últimos 5 exámenes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            examenes.forEach { examen ->
                ExamenCard(examen)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ExamenCard(examen: Examen) {
    val totalPreguntas = examen.aciertos + examen.fallos
    val porcentajeAciertos = if (totalPreguntas > 0) {
        (examen.aciertos.toDouble() / totalPreguntas * 100)
    } else 0.0

    val barColor = when {
        porcentajeAciertos >= 80 -> Color(0xFF4CAF50)
        porcentajeAciertos >= 50 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xD0484242)),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = examen.modulos,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = examen.fechaFormateada(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = obtenerNota(porcentajeAciertos) + "  "+ String.format("%.1f%%", porcentajeAciertos),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = barColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "✅ Aciertos: ${examen.aciertos}",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = "❌ Fallos: ${examen.fallos}",
                    fontSize = 12.sp,
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}