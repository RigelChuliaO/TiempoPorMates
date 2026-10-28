package com.wezen.tiempopormates.ui.theme.Menu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.Gemini.GeminiServiceImpl
import com.wezen.tiempopormates.data.Gemini.HistoriasCache
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.data.entity.Module
import com.wezen.tiempopormates.domain.model.usecase.ObtenerModulosCase
import com.wezen.tiempopormates.domain.model.usecase.SeleccionarOperacionIA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "MenuViewModel"

data class MenuUiState(
    val isLoading: Boolean = false,
    val userid: Long? = null,
    val configUser: ConfiguracionUsuario? = null,
    val user: User?= null,
    val error: String? = null,
    val mostrarContraseñaDialogo: Boolean = false,
    val modules: List<Module> = emptyList(),
    val errorContraseña: Boolean= false,
    val escontraseñaCorrecta: Boolean = false,
)
class MenuViewModel(

    private val userRepository: UserRepository,
    private val obtenerModulesCase: ObtenerModulosCase,
    private val seleccionarOperacionIA: SeleccionarOperacionIA

    ): ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    //Iniciamos para poder lanzar la consulta en segundo plano y que siempre tenga una historia cargada
    private val geminiService = GeminiServiceImpl()

    init {
        loadUser(uiState.value.userid ?: 0)
        _uiState.value = _uiState.value.copy(modules = obtenerModulesCase())

        //Lanzamos la consulta a Gemini, ya sabemos el usuario y vamos adelantando
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

                    IniciarAI(configUser)
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
    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = userRepository.CargarUsuario(userId)
                val modules = obtenerModulesCase()

                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        modules = modules,
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
    //Funciones para mostra dialogo acceso a configuracion

    fun closeContraseñaDialog() {
        _uiState.value = _uiState.value.copy(mostrarContraseñaDialogo = false)
    }

    fun openContraseñaDialog() {
        _uiState.value = _uiState.value.copy(mostrarContraseñaDialogo = true)
    }

    fun IniciarAI(configUser: ConfiguracionUsuario) {
        Log.d(TAG, "Iniciando AI...")

        viewModelScope.launch {
            try {
                // Primera historia
                val operacion1 = seleccionarOperacionIA.invoke(configUser.ejerciciosComprension)
                val resultado1 = geminiService.obtenerHistoria(operacion1)
                if (resultado1.isSuccess) {
                    HistoriasCache.historia1 = resultado1.getOrNull()
                    Log.d(TAG, "Historia 1 precargada")
                    Log.d(TAG, "Historia 1: ${HistoriasCache.historia1?.historia}")
                    Log.d(TAG, "respuesta1: ${HistoriasCache.historia1?.respuesta}")
                }


                // Esperar 2 segundos entre llamadas para respetar rate limit
                Thread.sleep(2000)
                // Segunda historia
                val operacion2 = seleccionarOperacionIA.invoke(configUser.ejerciciosComprension)
                val resultado2 = geminiService.obtenerHistoria(operacion2)
                if (resultado2.isSuccess) {
                    HistoriasCache.historia2 = resultado2.getOrNull()
                    Log.d(TAG, "Historia 2 precargada")
                    Log.d(TAG, "Historia 2: ${HistoriasCache.historia2?.historia}")
                    Log.d(TAG, "respuesta2: ${HistoriasCache.historia1?.respuesta}")

                }
            } catch (e: Exception) {
                Log.e(TAG, "Error precargando historias: ${e.message}")
            }
        }
    }
}