package com.wezen.tiempopormates.ui.theme.Examenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.ExamenUsesCases


class ExamenViewModelFactory(
    private val userRepository: UserRepository,
    private val databaseHelper: DatabaseHelper,
    private val usecasesExamen: ExamenUsesCases,
)  : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExamenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExamenViewModel(
                userRepository,
                usecasesExamen,
                databaseHelper,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

