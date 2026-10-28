package com.wezen.tiempopormates.ui.theme.Examenes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.ConfigUsuarioExamen
import com.wezen.tiempopormates.data.entity.PruebaExamen
import com.wezen.tiempopormates.data.entity.ResultadosExamen
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.ExamenUsesCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ExamenVM"

data class ExamenUistates(
    val isLoading: Boolean = false,
    val userid: Long? = null,
    val pruebas: List<PruebaExamen> = emptyList(),
    val user: User? = null,
    val configUser: ConfiguracionUsuario? = null,
    val isValidating: Boolean = false,
    val error: String? = null,
    val examenFinalizado: Boolean = false,
    val indicePruebaActual: Int = 0,
    val resultados: ResultadosExamen? = null,
    val yaGuardado: Boolean = false

    )
class ExamenViewModel(
    private val userRepository: UserRepository,
    private val usecasesExamen: ExamenUsesCases,
    private val databaseHelper: DatabaseHelper

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamenUistates())
    val uiState: StateFlow<ExamenUistates> = _uiState.asStateFlow()

    fun CargarUsuario(userId: Long) {
        Log.d(TAG, "Cargando usuario con ID: $userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = userRepository.CargarUsuario(userId)
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        userid= userId,
                        user = user,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Usuario no encontrado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun CargarConfigUsuario(userId: Long) {
        Log.d(TAG, "Cargando Configuracion usuario con ID: $userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val configUser = userRepository.CargarConfigUsuario(userId)
                if (configUser != null) {
                    _uiState.value = _uiState.value.copy(
                        configUser = configUser,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Configuracion del Usuario no encontrado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun GenerarPruebasExamen(){
        val nuevasPruebas= usecasesExamen.generarPruebasExamen(
                _uiState.value.configUser!!.numeroPreguntasExamen,
                _uiState.value.configUser!!.listadoModulosExamen
        )
        _uiState.value = _uiState.value.copy(pruebas = nuevasPruebas)
        Log.d(TAG, "Pruebas generadas: ${nuevasPruebas.size}")
    }

    fun obtenerPruebaActual(): PruebaExamen? {
        val indiceActual = _uiState.value.indicePruebaActual
        return if (indiceActual < _uiState.value.pruebas.size) {
            _uiState.value.pruebas[indiceActual]
        } else {
            null
        }
    }

    fun registrarRespuesta(esCorrecto: Boolean) {
        val pruebas = _uiState.value.pruebas.toMutableList()
        val indiceActual = _uiState.value.indicePruebaActual

        if (indiceActual < pruebas.size) {
            // Actualizar la prueba actual con la respuesta
            pruebas[indiceActual] = pruebas[indiceActual].copy(
                respondida = true,
                esCorrecto = esCorrecto
            )

            // Actualizar estado
            _uiState.value = _uiState.value.copy(pruebas = pruebas)

            Log.d(
                TAG,
                "Respuesta registrada - Prueba ${indiceActual + 1}/${_uiState.value.configUser?.numeroPreguntasExamen}: " +
                        "${pruebas[indiceActual].modulo} = ${if (esCorrecto) "✓ CORRECTA" else "✗ INCORRECTA"}"
            )
        }
    }
    fun siguientePrueba() {
        viewModelScope.launch {
            val indiceActual = _uiState.value.indicePruebaActual
            val totalPruebas = _uiState.value.configUser?.numeroPreguntasExamen ?: 0
            Log.d(TAG, "Avanzando desde prueba ${indiceActual + 1}/$totalPruebas")

            if (indiceActual < totalPruebas - 1) {
                // Hay más pruebas, ir a la siguiente
                _uiState.value = _uiState.value.copy(
                    indicePruebaActual = indiceActual + 1
                )
                Log.d(TAG, "Mostrando prueba ${indiceActual + 2}/$totalPruebas")
            } else {
                // Era la última prueba, finalizar examen
                Log.d(TAG, "Última prueba alcanzada, finalizando examen")
                finalizarExamen()
            }
        }
    }

    private fun finalizarExamen() {
        viewModelScope.launch {
            try {
                val pruebas = _uiState.value.pruebas
                val totalPruebas = pruebas.size
                val pruebasCorrectas = pruebas.count { it.esCorrecto }
                val pruebasIncorrectas = totalPruebas - pruebasCorrectas

                val porcentajeAcierto = if (totalPruebas > 0) {
                    (pruebasCorrectas.toFloat() / totalPruebas * 100).toInt()
                } else {
                    0
                }

                val resultados = ResultadosExamen(
                    totalPruebas = totalPruebas,
                    pruebasCorrectas = pruebasCorrectas,
                    pruebasIncorrectas = pruebasIncorrectas,
                    porcentajeAcierto = porcentajeAcierto
                )

                _uiState.value = _uiState.value.copy(
                    examenFinalizado = true,
                    resultados = resultados
                )

                Log.d(
                    TAG,
                    "EXAMEN FINALIZADO:\n" +
                            "  Total: $totalPruebas\n" +
                            "  Correctas: $pruebasCorrectas\n" +
                            "  Incorrectas: $pruebasIncorrectas\n" +
                            "  Porcentaje: $porcentajeAcierto%"
                )

                // Desglose por operación
                val desglose = mutableMapOf<String, Pair<Int, Int>>()
                pruebas.forEach { prueba ->
                    val (correctas, total) = desglose.getOrDefault(prueba.modulo, 0 to 0)
                    desglose[prueba.modulo] =
                        (if (prueba.esCorrecto) correctas + 1 else correctas) to (total + 1)
                }

                Log.d(TAG, "Desglose por operación:")
                desglose.forEach { (tipo, valores) ->
                    val (correctas, total) = valores
                    Log.d(TAG, "  $tipo: $correctas/$total")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error finalizando examen: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error finalizando examen: ${e.message}"
                )
            }
        }
    }
    fun guardarResultadosEnBD() {
        viewModelScope.launch {
            try {
                val user = _uiState.value.user
                val resultados = _uiState.value.resultados
                val pruebas = _uiState.value.pruebas

                if (user != null && resultados != null) {
                    Log.d(TAG, "Guardando resultados en BD...")

                    // Obtener los módulos únicos del examen
                    val modulosExamen = pruebas
                        .map { it.modulo }
                        .distinct()
                        .joinToString(",")

                    // Preparar datos para guardar
                    val updateData = mapOf(
                        "aciertos" to resultados.pruebasCorrectas.toString(),
                        "fallos" to resultados.pruebasIncorrectas.toString(),
                        "modulos" to modulosExamen,
                        "tiempo" to "" // Se puede agregar tiempo si es necesario
                    )

                    Log.d(TAG, "Datos a guardar: $updateData")

                    // Guardar en BD
                    databaseHelper.insertarExamen(
                        usuarioId = user.id,
                        updates = updateData
                    )

                    _uiState.value = _uiState.value.copy(yaGuardado = true)

                    Log.d(TAG, "Resultados guardados exitosamente en BD para usuario ${user.nombre}")
                } else {
                    Log.e(TAG, "Error: usuario o resultados null")
                    _uiState.value = _uiState.value.copy(
                        error = "Error: Datos incompletos"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error guardando resultados: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error guardando resultados: ${e.message}"
                )
            }
        }
    }
}
