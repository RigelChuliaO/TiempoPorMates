package com.wezen.tiempopormates.ui.theme.Dividir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.CorregirDivisionUseCase


class DividirViewModelFactory(
    private val dbHelper: DatabaseHelper,

    ) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            DividirViewModel::class.java -> {
                val repository = UserRepository(dbHelper)


                DividirViewModel(
                    repository,
                    CorregirDivisionUseCase(dbHelper)

                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}