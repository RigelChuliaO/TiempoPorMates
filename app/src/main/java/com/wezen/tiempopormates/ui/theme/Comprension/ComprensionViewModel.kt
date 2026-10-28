package com.wezen.tiempopormates.ui.theme.Comprension

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.Gemini.GeminiServiceImpl
import com.wezen.tiempopormates.data.Gemini.HistoriaComprension
import com.wezen.tiempopormates.data.Gemini.HistoriasCache
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.SeleccionarOperacionIA
import com.wezen.tiempopormates.domain.model.usecase.corregirComprensionCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ComprensionViewModel"

data class ComprensionUistates(
    val isLoading: Boolean = false,
    val userid: Long? = null,
    val user: User? = null,
    val configUser: ConfiguracionUsuario? = null,
    val historiaEnPantalla: HistoriaComprension? = null,
    val cambiarHistoria: Boolean = false,
    val comprensionCorregida: Boolean = false,
    val comprensionCorrecta: Boolean? = false,
    val error: String? = null,
    val isValidating: Boolean = false,

    )
class ComprensionViewModel(
    private val userRepository: UserRepository,
    private val corregirComprensioncase: corregirComprensionCase,
    private val seleccionarOperacionIA: SeleccionarOperacionIA

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(ComprensionUistates())
    val uiState: StateFlow<ComprensionUistates> = _uiState.asStateFlow()

    init {
        leerHistoria()
        CargarUsuario(uiState.value.userid ?: 0)

    }

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

    fun leerHistoria() {
        viewModelScope.launch {

            Log.d(TAG, "Leyendo historia del caché...")

            val operacionIA = seleccionarOperacionIA(_uiState.value.configUser?.ejerciciosComprension ?: "")

            // Cargamos la historia 1
            if (HistoriasCache.historia1 != null) {
                _uiState.value = _uiState.value.copy(
                    historiaEnPantalla = HistoriasCache.historia1,
                    isValidating = true,
                    cambiarHistoria = false,
                    comprensionCorregida = false

                )
                HistoriasCache.historia1Null() // Borramos la historia
                // Preparamos a la IA y le pedimos una historia
                val geminiService = GeminiServiceImpl()
                val resultado = geminiService.obtenerHistoria(operacionIA)

                HistoriasCache.historia1 = resultado.getOrNull()

            }else { // en caso de que la historia 1 no este cargamos la 2
                _uiState.value = _uiState.value.copy(
                    historiaEnPantalla = HistoriasCache.historia2,
                    isValidating = true,
                    cambiarHistoria = false,
                    comprensionCorregida = false
                )
                HistoriasCache.historia2Null() // Borramos la historia
                // Preparamos a la IA y le pedimos una historia
                val geminiService = GeminiServiceImpl()
                val resultado = geminiService.obtenerHistoria(operacionIA)

                HistoriasCache.historia1 = resultado.getOrNull()
            }

        }
    }

    fun corregirComprension(respuestaUsuario: String) {
        val configUser = uiState.value.configUser ?: return
        val respuestaIA = uiState.value.historiaEnPantalla?.respuesta.toString()
        val esCorrecta = corregirComprensioncase(respuestaUsuario,respuestaIA,configUser )
        Log.d(TAG, "Respuesta Usuario: $respuestaUsuario : Respuesta solucion: ${uiState.value.historiaEnPantalla?.respuesta}-> Resultado $esCorrecta")

        _uiState.value = _uiState.value.copy(
            comprensionCorregida = true,
            comprensionCorrecta = esCorrecta,
            cambiarHistoria = true
        )
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

}