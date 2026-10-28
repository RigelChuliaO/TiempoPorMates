package com.wezen.tiempopormates.ui.theme.Configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.GuardarConfiguracionUseCase
import com.wezen.tiempopormates.domain.model.usecase.ResetearTiempoUseCase

class ConfigViewModelFactory(private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ConfigViewModel::class.java -> {
                val repository = UserRepository(dbHelper)
                val guardarConfiguracionUseCase = GuardarConfiguracionUseCase(dbHelper)
                val resetearTiempoUseCase = ResetearTiempoUseCase(dbHelper)

                ConfigViewModel(
                    repository,
                    guardarConfiguracionUseCase,
                    resetearTiempoUseCase,
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}