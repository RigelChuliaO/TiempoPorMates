package com.wezen.tiempopormates.ui.theme.Estadisticas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.DatosUsuarioModulo
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.entity.EstadisticaTabla
import com.wezen.tiempopormates.data.entity.Examen
import com.wezen.tiempopormates.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.wezen.tiempopormates.domain.model.usecase.CalculoEstadisticasGeneralesTablasUser
import com.wezen.tiempopormates.domain.model.usecase.CalculoMejorYPeorTabla
import kotlinx.coroutines.flow.asStateFlow


private const val TAG = "EstadisticasViewModel"

data class EstadisticasUiState(
    val user: User? = null,
    val configUser: ConfiguracionUsuario? = null,
    val datosUsuarioModulo: DatosUsuarioModulo?= null,
    val estadisticasTablas: Map<Int, EstadisticaTabla> = emptyMap(),
    val examenes: List<Examen> = emptyList(),
    val ejerciciosTotalesTablas: Int = 0,
    val fallosTotalesTablas: Int = 0,
    val porcentajeTotalTablas: Double = 0.0,
    val mejorArea: String = "",
    val areaAMejorar: String = "",
    val mejorTabla: String = "",
    val peorTabla: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
class EstadisticasViewModel(
    private val userRepository: UserRepository,


) : ViewModel() {

    private val _uiState = MutableStateFlow(EstadisticasUiState())
    val uiState: StateFlow<EstadisticasUiState> = _uiState

    private val _expandedSection = MutableStateFlow<String?>(null)
    val expandedSection: StateFlow<String?> = _expandedSection

    private val _selectedTablaFilter = MutableStateFlow<Int?>(null)
    val selectedTablaFilter: StateFlow<Int?> = _selectedTablaFilter

    private val _datosModulo = MutableStateFlow(DatosUsuarioModulo())

    //val datosModulo: StateFlow<DatosUsuarioModulo> = _datosModulo

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
                    CargarConfigUsuario(userId)

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
    fun CargarDatosModulos(userId: Long) {
        Log.d(TAG, "Cargando Daos Modulos de usuario con ID: $userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val datosUsuarioModulo = userRepository.CargarDatosUsuario(userId)
                if (datosUsuarioModulo != null) {
                    _uiState.value = _uiState.value.copy(
                        datosUsuarioModulo = datosUsuarioModulo,
                        isLoading = false,
                        error = null
                    )
                    Log.d(TAG, "DAatos Usuario Modulos: ${datosUsuarioModulo}")
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

    fun CargarExamenes(userId: Long) {
        Log.d(TAG, "Cargando Examenes de usuario con ID: $userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = userRepository.CargarUsuario(userId)
                val examenes = userRepository.obtenerUltimosExamenesUsuario(userId, limite = 5)
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        examenes = examenes,
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

    fun cargarEstadisticasTablas(userId: Long) {
       viewModelScope.launch {
           _uiState.value = _uiState.value.copy(isLoading = true, error = null)
           try {
               val estadisticasTablas = userRepository.obtenerEstadisticasTablasUsuario(userId)
               if (userId != null) {
                   val (mejorTabla, peorTabla) = CalculoMejorYPeorTabla(estadisticasTablas)
                   val (ejerciciosTotales, fallosTotales, porcentajeTotal)= CalculoEstadisticasGeneralesTablasUser( estadisticasTablas)

                   _uiState.value = _uiState.value.copy(
                       estadisticasTablas = estadisticasTablas,
                       ejerciciosTotalesTablas = ejerciciosTotales,
                       fallosTotalesTablas = fallosTotales,
                       porcentajeTotalTablas = porcentajeTotal,
                       mejorTabla = mejorTabla,
                       peorTabla = peorTabla,
                       isLoading = false
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
                   error = e.message ?: "Error al cargar estadísticas en las tablas"
               )
           }
       }
   }

    fun toggleSection(section: String) {
        _expandedSection.value = if (_expandedSection.value == section) null else section
    }

    fun selectTabla(tabla: Int?) {
        _selectedTablaFilter.value = if (_selectedTablaFilter.value == tabla) null else tabla
    }

}