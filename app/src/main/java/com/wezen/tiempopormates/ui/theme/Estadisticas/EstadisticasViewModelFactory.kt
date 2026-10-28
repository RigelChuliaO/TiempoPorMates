package com.wezen.tiempopormates.ui.theme.Estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.repository.UserRepository


class EstadisticasViewModelFactory(
    private val dbHelper: DatabaseHelper,
    ) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            EstadisticasViewModel::class.java -> {
                val repository = UserRepository(dbHelper)
                /*val generarNumerosRestaUseCase = GenerarNumerosRestaUseCase(dbHelper)
                val corregirSumaUseCase = CorregirRestaUseCase(dbHelper)*/

                EstadisticasViewModel(
                    repository,
                    /*generarNumerosRestaUseCase,
                    corregirSumaUseCase*/
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}