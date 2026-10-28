package com.wezen.tiempopormates.ui.theme.Inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CreateUserCase
import com.wezen.tiempopormates.domain.model.usecase.DeleteUserCase
import com.wezen.tiempopormates.domain.usecase.GetAllUsersCase


class InicioViewModelFactory(private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            InicioViewModel::class.java -> {
                // Crear las dependencias en orden
                val repository = UserRepository(dbHelper)
                val getAllUsersCase = GetAllUsersCase(repository)
                val createNewUser = CreateUserCase(repository)
                val deleteNewUser = DeleteUserCase(repository)


                // Crear el ViewModel con las dependencias
                InicioViewModel(
                    repository,
                    getAllUsersCase,
                    createNewUser,
                    deleteNewUser

                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}