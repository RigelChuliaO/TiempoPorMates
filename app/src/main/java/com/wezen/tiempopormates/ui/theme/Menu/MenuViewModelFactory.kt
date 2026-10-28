package com.wezen.tiempopormates.ui.theme.Menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.ValidarContraseñaUserCase
import com.wezen.tiempopormates.domain.model.usecase.ObtenerModulosCase
import com.wezen.tiempopormates.domain.model.usecase.SeleccionarOperacionIA


class MenuViewModelFactory(
    private val dbHelper: DatabaseHelper,
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MenuViewModel::class.java -> {
                val repository = UserRepository(dbHelper)
                val obtenerModulosCase = ObtenerModulosCase()
                val SeleccionarOperacionIA = SeleccionarOperacionIA()

                MenuViewModel(
                    repository,
                    obtenerModulosCase,
                    SeleccionarOperacionIA


                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
