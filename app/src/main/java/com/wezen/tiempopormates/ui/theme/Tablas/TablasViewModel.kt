package com.wezen.tiempopormates.ui.theme.Tablas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.domain.model.usecase.GenerarOperacionTabla
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirOperacionTablasUseCase
import com.wezen.tiempopormates.domain.model.usecase.NumeroTabla

private const val TAG = "TablasViewModel"

data class TablasUiState(
    val isLoading: Boolean = false,
    val userid: Long? = null,
    val user: User? = null,
    val configUser: ConfiguracionUsuario? = null,
    val operacionTabla: NumeroTabla? = null,
    val respuesta: String = "",
    val operacionTablaCorregida: Boolean = false,
    val error: String? = null,
    val esCorrecta: Boolean? = true,
    val isValidating: Boolean = false

)

class TablasViewModel(
    private val userRepository: UserRepository,
    private val generarOperacionTabla: GenerarOperacionTabla,
    private val corregirOperacionTablasUseCase: CorregirOperacionTablasUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(TablasUiState())
    val uiState: StateFlow<TablasUiState> = _uiState.asStateFlow()

    fun CargarUsuario(userId: Long) {
        Log.d(TAG, "Cargando usuario con ID: $userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = userRepository.CargarUsuario(userId)
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
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
    fun generarNuevaOperacionTabla() {

        Log.d(TAG, "Generando Numeros nueva operacion tabla")
        val user = _uiState.value.user ?: return
        val configUser = _uiState.value.configUser ?: return

        viewModelScope.launch {
            try {
                val operacion = generarOperacionTabla(user, configUser)

                _uiState.value = _uiState.value.copy(
                    operacionTabla = operacion,
                    respuesta = "",
                    operacionTablaCorregida = false,
                    esCorrecta = null,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error generando multiplicación: ${e.message}"
                )
            }
        }
    }

    /**
     * Actualiza la respuesta introducida por el usuario
     * @param nuevoValor Nuevo valor (máximo 2 dígitos)
     */
    fun actualizarRespuesta(nuevoValor: String) {
        if (nuevoValor.length <= 2 && nuevoValor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(respuesta = nuevoValor)
        }
    }

    /**
     * Corrige la multiplicación introducida por el usuario
     */
    fun corregirOperacionTabla() {
        val user = _uiState.value.user ?: return
        val configUser = _uiState.value.configUser ?: return
        val operacion = _uiState.value.operacionTabla ?: return
        val respuestaStr = _uiState.value.respuesta

        // Validar que no esté vacío
        if (respuestaStr.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                error = "Debes introducir una respuesta"
            )
            return
        }

        viewModelScope.launch {
            try {
                val respuesta = respuestaStr.toInt()

                val resultado = corregirOperacionTablasUseCase(
                    respuesta,
                    user,
                    operacion.tabla,
                    operacion.multiplicador,
                    operacion.resultado,
                    configUser

                )

                _uiState.value = _uiState.value.copy(
                    operacionTablaCorregida = true,
                    esCorrecta = resultado,
                    error = null,
                    isValidating = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error corrigiendo multiplicación: ${e.message}",
                )
            }
        }
    }

    fun siguienteOperacionTabla() {
        generarNuevaOperacionTabla()
    }
}