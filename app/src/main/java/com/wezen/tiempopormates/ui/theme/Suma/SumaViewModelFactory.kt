package com.wezen.tiempopormates.ui.theme.Suma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirSumaUseCase
import com.wezen.tiempopormates.domain.model.usecase.GenerarNumerosSumaUseCase


class SumaViewModelFactory(
    private val dbHelper: DatabaseHelper,

) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SumaViewModel::class.java -> {
                val repository = UserRepository(dbHelper)
                val generarNumerosSumaUseCase = GenerarNumerosSumaUseCase(dbHelper)
                val corregirSumaUseCase = CorregirSumaUseCase(dbHelper)

                SumaViewModel(
                    repository,
                    generarNumerosSumaUseCase,
                    corregirSumaUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}