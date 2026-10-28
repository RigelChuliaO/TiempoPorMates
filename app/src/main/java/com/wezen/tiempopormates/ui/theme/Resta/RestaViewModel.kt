package com.wezen.tiempopormates.ui.theme.Resta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirRestaUseCase
import com.wezen.tiempopormates.domain.model.usecase.GenerarNumerosRestaUseCase
import com.wezen.tiempopormates.domain.model.usecase.NumerosResta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "RestaViewModel"

data class RestaUistates(
    val isLoading: Boolean = false,
    val userid: Long? = null,
    val user: User? = null,
    val configUser: ConfiguracionUsuario? = null,
    val numeros: NumerosResta? = null,
    val valores: List<String> = emptyList(),
    val erroresPorCifra: List<Boolean> = emptyList(),
    val restaCorregida: Boolean = false,
    val error: String? = null,
    val todosCorrectos: Boolean? = null,
    val isValidating: Boolean = false,

    )
class RestaViewModel(
    private val userRepository: UserRepository,
    private val generarNumerosRestaUseCase: GenerarNumerosRestaUseCase,
    private val corregirRestaUseCase: CorregirRestaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaUistates())
    val uiState: StateFlow<RestaUistates> = _uiState.asStateFlow()

    init {
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
    fun generarNuevasRestas() {
        val user = _uiState.value.user ?: return
        val configUser = _uiState.value.configUser ?: return


        viewModelScope.launch {
            try {
                val numeros = generarNumerosRestaUseCase(user,configUser)
                val nuevosValores = List(configUser.numeroCifrasResta + 1) { "" }

                _uiState.value = _uiState.value.copy(
                    numeros = numeros,
                    valores = nuevosValores,
                    erroresPorCifra = List(configUser.numeroCifrasResta + 1) { false },
                    restaCorregida = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error generando Resta: ${e.message}"
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
    fun corregirResta() {
        val user = _uiState.value.user ?: return
        val configUser = _uiState.value.configUser ?: return
        val numeros = _uiState.value.numeros ?: return
        val valores = _uiState.value.valores

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isValidating = true)

            try {
                val num1Str = numeros.minuendo.toString().padStart(configUser.numeroCifrasResta, ' ')
                val num2Str = numeros.sustraendo.toString().padStart(configUser.numeroCifrasResta, ' ')

                val resultado = corregirRestaUseCase(
                    valores,
                    num1Str,
                    num2Str,
                    numeros.resultado,
                    user,
                    configUser
                )

                _uiState.value = _uiState.value.copy(
                    restaCorregida = true,
                    todosCorrectos = resultado.todosCorrectos,
                    erroresPorCifra = resultado.errores,
                    isValidating = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error corrigiendo Resta: ${e.message}",
                    isValidating = false
                )
            }
        }
    }

    fun actualizarValor(indice: Int, nuevoValor: String) {

        if (nuevoValor.length <= 1 && nuevoValor.all { it.isDigit() }) {
            val nuevosValores = _uiState.value.valores.toMutableList()
            if (indice < nuevosValores.size) {
                nuevosValores[indice] = nuevoValor
                _uiState.value = _uiState.value.copy(valores = nuevosValores)
            }
        }
    }
    fun siguienteResta() {
        generarNuevasRestas()
    }

}