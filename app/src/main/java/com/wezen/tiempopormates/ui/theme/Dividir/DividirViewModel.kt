package com.wezen.tiempopormates.ui.theme.Dividir

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirDivisionUseCase
import com.wezen.tiempopormates.domain.model.usecase.GenerarNuevaDivision
import com.wezen.tiempopormates.domain.model.usecase.DatosDivision
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


private const val TAG = "DividirViewModel"

data class DividirUistates(
    val isLoading: Boolean = false,
    val userid: Long? = null,
    val configUser: ConfiguracionUsuario? = null,
    val user: User? = null,
    val DatosDivision: DatosDivision? = null,
    val valoresCociente: List<String> = emptyList(),//Estos son los que rellena el usuario
    val valoresResto: List<String> = emptyList(),
    val valoresUsuarioCanvas: List<String> = emptyList(),
    val erroresResto: List<Boolean> = emptyList(),
    val erroresCociente: List<Boolean> = emptyList(),
    val erroresCanvas: List<Boolean> = emptyList(),
    val erroresPorCifra: List<Boolean> = emptyList(),
    val divisionCorregida: Boolean = false,
    val error: String? = null,
    val todosCorrectos: Boolean? = null,
    val isValidating: Boolean = false

)class DividirViewModel(
    private val userRepository: UserRepository,
    private val corregirDivisionUseCase: CorregirDivisionUseCase,

) : ViewModel() {

    private val _uiState = MutableStateFlow(DividirUistates())
    val uiState: StateFlow<DividirUistates> = _uiState.asStateFlow()

    init {
        CargarUsuario(uiState.value.userid ?: 0)
        CargarConfigUsuario(uiState.value.userid ?: 0)
    }

    fun generarNuevaDivision() {
        val configUser = _uiState.value.configUser ?: return

        viewModelScope.launch {
            try {
                val numeros = GenerarNuevaDivision(configUser)
                inicializarValoresCociente(numeros.cociente.toString().length)
                inicializarValoresCanvas(numeros.dividendo.toString().length*(numeros.cociente.toString().length*2))
                inicializarValoresResto(numeros.dividendo.toString().length)

                Log.d(TAG, "Dividendo: ${numeros.dividendo}, ${numeros.dividendo.toString().length}")
                Log.d(TAG, "Divisor: ${numeros.divisor}, ${numeros.divisor.toString().length}")
                Log.d(TAG, "Cociente: ${numeros.cociente}, ${numeros.cociente.toString().length}")
                Log.d(TAG, "Resto: ${numeros.resto}, ${numeros.resto.toString().length}")
                Log.d(TAG, "Tamaño del canvas : ${numeros.divisor.toString().length*(numeros.cociente.toString().length*2)}")
                Log.d(TAG, "Tamaño del cociente : ${numeros.cociente.toString().length}")
                Log.d(TAG, "Tamaño del canvas : ${numeros.dividendo.toString().length*(numeros.cociente.toString().length*2)}")
                Log.d(TAG, "Tamaño del resto : ${numeros.dividendo.toString().length}")



                _uiState.value = _uiState.value.copy(
                    DatosDivision = numeros,
                    divisionCorregida = false,
                    todosCorrectos = null,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error generando Division: ${e.message}"
                )
            }
        }
    }
    fun CargarUsuario(userId: Long) {
        Log.d(TAG, "Cargando usuario para dividir con ID: $userId")
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
                        error = "Usuario para dividir no encontrado"
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
    fun corregirDivision(){

        val user = _uiState.value.user ?: return
        val configUser = _uiState.value.configUser ?: return
        val datosDivision = _uiState.value.DatosDivision ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {

                Log.d(TAG, "Dividendo:  ${_uiState.value.DatosDivision?.dividendo} Divisor: ${_uiState.value.DatosDivision?.divisor}")
                Log.d(TAG, "Cociente:  ${_uiState.value.DatosDivision?.cociente}  Resto: ${_uiState.value.DatosDivision?.resto}")
                Log.d(TAG, "Canvas:  ${_uiState.value.valoresUsuarioCanvas} ")

                val resultado = corregirDivisionUseCase(
                    _uiState.value.valoresUsuarioCanvas,
                    _uiState.value.valoresCociente,
                    _uiState.value.valoresUsuarioCanvas,
                    datosDivision,
                    user,
                    configUser
                )

                _uiState.value = _uiState.value.copy(
                        divisionCorregida = true,
                        todosCorrectos = resultado.divisionCorrecta,
                        isLoading = false,
                        error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }

    }

    fun inicializarValoresCociente(x: Int) {
        _uiState.value = _uiState.value.copy(
            valoresCociente = List(x) { "" }
        )
        Log.e(TAG, "Borro el cociente y lo pongo a 0")


    }

    //Para lo que introduce el usuario en la matriz
    fun actualizarValorCociente(indice:Int,valor:String){

        // Solo permitir un dígito o vacío
        if (valor.isEmpty() || (valor.length == 1 && valor[0].isDigit())) {
            val nuevosValores = _uiState.value.valoresCociente.toMutableList()
            if (indice < nuevosValores.size) {
                nuevosValores[indice] = valor
                _uiState.value = _uiState.value.copy(valoresCociente = nuevosValores)
            }
        }

    }
    fun inicializarValoresResto(x: Int) {
        _uiState.value = _uiState.value.copy(
            valoresResto = List(x) { "" }
        )
    }
    fun actualizarValorResto(index:Int,valor:String){
        val valoresActual = _uiState.value.valoresResto.toMutableList()
        valoresActual[index] = valor.toString()

        _uiState.value = _uiState.value.copy(valoresResto = valoresActual)
    }

    fun inicializarValoresCanvas(x: Int) {
        _uiState.value = _uiState.value.copy(
            valoresUsuarioCanvas = List(x) { "" }
        )
    }
    fun actualizarValorCanvas(indice:Int,valor:String){

        // Solo permitir un dígito o vacío
        if (valor.isEmpty() || (valor.length == 1 && valor[0].isDigit())) {
            val nuevosValores = _uiState.value.valoresUsuarioCanvas.toMutableList()
            if (indice < nuevosValores.size) {
                nuevosValores[indice] = valor
                _uiState.value = _uiState.value.copy(valoresUsuarioCanvas = nuevosValores)
            }
        }

    }

}