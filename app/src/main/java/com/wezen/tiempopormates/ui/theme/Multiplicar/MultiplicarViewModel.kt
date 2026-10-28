package com.wezen.tiempopormates.ui.theme.Multiplicar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirMultiplicacionUseCase

import com.wezen.tiempopormates.domain.model.usecase.GenerarNumerosMultiplicacionUseCase
import com.wezen.tiempopormates.domain.model.usecase.NumerosMultiplicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


private const val TAG = "MultiplicarViewModel"

data class MultiplicarUiState(
    val isLoading: Boolean = false,
    val userid: Long? = null,
    val user: User? = null,
    val configUser: ConfiguracionUsuario? = null,
    val numeros: NumerosMultiplicacion? = null,
    val valores: List<String> = emptyList(),
    val valoresResultado: List<String> = emptyList(),
    val erroresResultado: List<Boolean> = emptyList(),
    val erroresIntermedios: List<Boolean> = emptyList(),
    val multiplicacionCorregida: Boolean = false,
    val error: String? = null,
    val todosCorrectos: Boolean? = null,
    val isValidating: Boolean = false
)

class MultiplicarViewModel(
    private val userRepository: UserRepository,
    private val generarNumerosMultiplicacionUseCase: GenerarNumerosMultiplicacionUseCase,
    private val corregirMultiplicacionUseCase: CorregirMultiplicacionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultiplicarUiState())
    val uiState: StateFlow<MultiplicarUiState> = _uiState.asStateFlow()

    init {
        CargarUsuario(uiState.value.userid ?: 0)
        CargarConfigUsuario(uiState.value.userid ?: 0)
    }

    fun generarNuevasMultiplicaciones() {
        val user = _uiState.value.user ?: return
        val configUser = _uiState.value.configUser ?: return


        viewModelScope.launch {
            try {
                val numeros = generarNumerosMultiplicacionUseCase(user,configUser)
                // Para saber los valores de los cuadros de las respuestas
                val nuevosValores = List((configUser.numeroCifrasMultiplicando + 1) * configUser.numeroCifrasMultiplicador) { "" }
                // Para saber los valores de los cuadros de las respuestas
                val nuevosValoresResultado = List(configUser.numeroCifrasMultiplicando + configUser.numeroCifrasMultiplicador) { "" }

                _uiState.value = _uiState.value.copy(
                    numeros = numeros,
                    valores = nuevosValores,
                    valoresResultado = nuevosValoresResultado,
                    erroresResultado = List(configUser.numeroCifrasMultiplicando + 1) { false },
                    erroresIntermedios = List(configUser.numeroCifrasMultiplicando + 1) { false },
                    multiplicacionCorregida = false,
                    error = null,
                    todosCorrectos = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error generando multiplicación: ${e.message}"
                )
            }
        }
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
    fun corregirMultiplicacion() {
        val user = _uiState.value.user ?: return
        val configUser = _uiState.value.configUser ?: return
        val numeros = _uiState.value.numeros ?: return
        val valoresResultado = _uiState.value.valoresResultado
        val valoresParciales=_uiState.value.valores

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isValidating = true)

            try {
                val num1Str = numeros.multiplicando.toString().padStart(configUser.numeroCifrasMultiplicando, ' ')
                val num2Str = numeros.multiplicador.toString().padStart(configUser.numeroCifrasMultiplicador, ' ')

                Log.d(TAG, "Los números son $num1Str y $num2Str el resultado es: ${numeros.resultado}")
                Log.d(TAG, "valores del usuario introducidos $valoresResultado")
                Log.d(TAG, "Cifras Parciales introducidas $valoresParciales")
                val resultado = corregirMultiplicacionUseCase(

                    valoresResultado,
                    valoresParciales,
                    num1Str,
                    num2Str,
                    numeros.resultado,
                    user,
                    configUser
                )
                Log.d(TAG, "Todo : ${_uiState.value}")
                _uiState.value = _uiState.value.copy(
                    multiplicacionCorregida = true,
                    todosCorrectos = resultado.todosCorrectos,
                    erroresResultado = resultado.erroresResultado,
                    erroresIntermedios = resultado.erroresCifrasIntermedias,
                    isValidating = false,
                    error = null
                )

                Log.d(TAG, "Todo : ${_uiState.value}")

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error corrigiendo multiplicación: ${e.message}",
                    isValidating = false
                )
            }
        }
    }

    fun actualizarValor(indice: Int, nuevoValor: String) {
        val user = _uiState.value.user ?: return

        // Solo permitir un dígito o vacío
        if (nuevoValor.isEmpty() || (nuevoValor.length == 1 && nuevoValor[0].isDigit())) {
            val nuevosValores = _uiState.value.valores.toMutableList()
            if (indice < nuevosValores.size) {
                nuevosValores[indice] = nuevoValor
                _uiState.value = _uiState.value.copy(valores = nuevosValores)
            }
        }
    }

    fun actualizarValorResultado(indice: Int, nuevoValor: String) {

        if (nuevoValor.length <= 1 && nuevoValor.all { it.isDigit() }) {
            val nuevosValores = _uiState.value.valoresResultado.toMutableList()
            if (indice < nuevosValores.size) {
                nuevosValores[indice] = nuevoValor
                _uiState.value = _uiState.value.copy(valoresResultado = nuevosValores)
                Log.d(TAG, "Actualizando valor en el índice $indice con el valor $nuevoValor")
                Log.d(TAG, "Nuevos valores: ${_uiState.value.valoresResultado}")
            }
        }
    }
    fun CargarConfigUsuario(userId: Long) {
        Log.d(TAG, "Cargando Configuracionusuario con ID: $userId")
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
    fun siguienteMultiplicacion() {
        generarNuevasMultiplicaciones()
    }
}
