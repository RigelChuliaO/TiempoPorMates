package com.wezen.tiempopormates.ui.theme.Multiplicar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirMultiplicacionUseCase
import com.wezen.tiempopormates.domain.model.usecase.CorregirRestaUseCase
import com.wezen.tiempopormates.domain.model.usecase.GenerarNumerosMultiplicacionUseCase

class MultiplicarViewModelFactory (
    private val dbHelper: DatabaseHelper,
    ) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            com.wezen.tiempopormates.ui.theme.Multiplicar.MultiplicarViewModel::class.java -> {
                val repository = UserRepository(dbHelper)
                val generarNumerosMultiplicacionUseCase = GenerarNumerosMultiplicacionUseCase(dbHelper)
                val corregirMultiplicacionUseCase = CorregirMultiplicacionUseCase(dbHelper)

                MultiplicarViewModel(
                    repository,
                    generarNumerosMultiplicacionUseCase,
                    corregirMultiplicacionUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}