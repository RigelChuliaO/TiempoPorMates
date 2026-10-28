package com.wezen.tiempopormates.ui.theme.Configuracion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.GuardarConfiguracionUseCase
import com.wezen.tiempopormates.domain.model.usecase.ResetearTiempoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ConfigViewModel"

data class ConfigUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val configUser: ConfiguracionUsuario? = null,
    val cambiosRealizados: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val showDeleteTimeDialog: Boolean = false,
    val isResetting: Boolean = false,
    val saveSuccess: Boolean = false,

)

class ConfigViewModel(
    private val userRepository: UserRepository,
    private val guardarConfiguracionUseCase: GuardarConfiguracionUseCase,
    private val resetearTiempoUseCase: ResetearTiempoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigUiState())
    val uiState: StateFlow<ConfigUiState> = _uiState.asStateFlow()


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


    /**
     * Actualiza la configuración
     */
    fun actualizarConfiguracion(nueva: ConfiguracionUsuario) {
        val configActual = _uiState.value.configUser ?: return

        // Variable para saber cuando ha realizado algún cambio en alguna variable de configuración
        val cambios = (
                nueva.numeroCifrasSuma != configActual.numeroCifrasSuma ||
                        nueva.tiempoExtraSuma != configActual.tiempoExtraSuma ||
                        nueva.llevadasSuma != configActual.llevadasSuma ||
                        nueva.numeroCifrasResta != configActual.numeroCifrasResta ||
                        nueva.tiempoExtraResta != configActual.tiempoExtraResta ||
                        nueva.llevadasResta != configActual.llevadasResta ||
                        nueva.numeroCifrasMultiplicando != configActual.numeroCifrasMultiplicando ||
                        nueva.numeroCifrasMultiplicador != configActual.numeroCifrasMultiplicador ||
                        nueva.tiempoExtraMultiplicacion != configActual.tiempoExtraMultiplicacion ||
                        nueva.numeroCifrasDividendo != configActual.numeroCifrasDividendo ||
                        nueva.numeroCifrasDivisor != configActual.numeroCifrasDivisor ||
                        nueva.tiempoExtraDivision != configActual.tiempoExtraDivision ||
                        nueva.soloExactasDivision != configActual.soloExactasDivision ||
                        nueva.tablasMultiplicar != configActual.tablasMultiplicar ||
                        nueva.tiempoExtratablasMultiplicar != configActual.tiempoExtratablasMultiplicar ||
                        nueva.tiempoExtraComprension != configActual.tiempoExtraComprension ||
                        nueva.numeroPreguntasExamen != configActual.numeroPreguntasExamen ||
                        nueva.tiempoExtraExamen != configActual.tiempoExtraExamen ||
                        nueva.listadoModulosExamen != configActual.listadoModulosExamen ||
                        nueva.password != configActual.password
                )

        _uiState.value = _uiState.value.copy(
            configUser  = nueva,
            cambiosRealizados = cambios
        )
    }

    /**
     * Guarda la configuración en la base de datos
     */
    fun guardarConfiguracion() {
        val user = _uiState.value.user ?: return
        val configuracion = _uiState.value.configUser ?: return
        Log.e(TAG, "Guardando configuracion: $configuracion")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val resultado = guardarConfiguracionUseCase(user.id, configuracion)

                if (resultado) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = null,
                        saveSuccess = true,
                        cambiosRealizados = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Error guardando configuración"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    /**
     * Abre el diálogo de confirmación para resetear tiempo
     */
    fun abrirDialogoResetearTiempo() {
        _uiState.value = _uiState.value.copy(showDeleteTimeDialog = true)
    }

    /**
     * Cierra el diálogo de confirmación
     */
    fun cerrarDialogoResetearTiempo() {
        _uiState.value = _uiState.value.copy(showDeleteTimeDialog = false)
    }

    /**
     * Resetea el tiempo acumulado del usuario a 0
     */
    fun resetearTiempo() {
        val user = _uiState.value.user ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isResetting = true)

            try {
                val resultado = resetearTiempoUseCase(user.id, user.tiempo)

                if (resultado) {
                    // Actualizar el usuario localmente
                    val userActualizado = user.copy(tiempo = 0)
                    _uiState.value = _uiState.value.copy(
                        user = userActualizado,
                        isResetting = false,
                        error = null,
                        showDeleteTimeDialog = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isResetting = false,
                        error = "Error reseteando tiempo"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isResetting = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
}
