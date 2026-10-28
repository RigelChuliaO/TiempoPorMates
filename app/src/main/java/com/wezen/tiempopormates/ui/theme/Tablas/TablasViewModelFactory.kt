package com.wezen.tiempopormates.ui.theme.Tablas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirOperacionTablasUseCase
import com.wezen.tiempopormates.domain.model.usecase.CorregirRestaUseCase
import com.wezen.tiempopormates.domain.model.usecase.GenerarNumerosRestaUseCase
import com.wezen.tiempopormates.domain.model.usecase.GenerarOperacionTabla
import com.wezen.tiempopormates.ui.theme.Resta.RestaViewModel


class TablasViewModelFactory(
    private val dbHelper: DatabaseHelper,

    ) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            com.wezen.tiempopormates.ui.theme.Tablas.TablasViewModel::class.java -> {
                val repository = UserRepository(dbHelper)
                val generarOperacionTablaUseCase = GenerarOperacionTabla()
                val corregirOperacionTablaUseCase = CorregirOperacionTablasUseCase(dbHelper)

                TablasViewModel(
                    repository,
                    generarOperacionTablaUseCase,
                    corregirOperacionTablaUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}