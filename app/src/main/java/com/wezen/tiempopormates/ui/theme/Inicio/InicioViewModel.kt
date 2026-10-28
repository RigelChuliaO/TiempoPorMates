package com.wezen.tiempopormates.ui.theme.Inicio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CreateUserCase
import com.wezen.tiempopormates.domain.model.usecase.DeleteUserCase
import com.wezen.tiempopormates.domain.usecase.GetAllUsersCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "InicioViewModel"


data class InicioUiState(
    val users: List<User> = emptyList(),
    val configUser: ConfiguracionUsuario? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val userToDelete: Long? = null,
    val showCreateUserDialog: Boolean = false
)

class InicioViewModel(
    private val userRepository: UserRepository,
    private val getAllUseCase: GetAllUsersCase,
    private val createUseCase: CreateUserCase,
    private val DeleteUserCase: DeleteUserCase

    ): ViewModel(){

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        Log.d(TAG, "Vamos a cargar los usuarios")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val users = getAllUseCase()
                _uiState.value = _uiState.value.copy(
                    users = users,
                    isLoading = false,
                    error = null
                )

                Log.d(TAG, "Lista Users InicioVieModel ${users}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error cargando usuarios: ${e.message}"
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
    //Funciones para mostra y crear el nuevo usuario

    fun closeCreateUserDialog() {
        _uiState.value = _uiState.value.copy(showCreateUserDialog = false)
    }

    fun openCreateUserDialog() {
        _uiState.value = _uiState.value.copy(showCreateUserDialog = true)
    }

    fun createNewUser(nombre: String, avatar: String? = null) {
        viewModelScope.launch {
            try {
                val userId = createUseCase(nombre, avatar)
                if (userId > 0) {
                    loadUsers() // Recargar la lista
                    closeCreateUserDialog()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al crear el usuario"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    //Funciones para borrar el usuario
    fun closeDeleteUserDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun openDeleteUserDialog(userId: Long) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            userToDelete = userId
        )
    }

    fun DeleteUser() {
        val userId = _uiState.value.userToDelete ?: return
        Log.d(TAG, "Voy a borrrar a $userId")

        viewModelScope.launch {
            try {
                val deleted = DeleteUserCase(userId)
                if (deleted) {
                    loadUsers()
                    closeDeleteUserDialog()

                }
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message}"
                )
            }
            closeDeleteUserDialog()

        }

    }
}