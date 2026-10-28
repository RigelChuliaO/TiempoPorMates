package com.wezen.tiempopormates.ui.theme.Comprension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.Gemini.GeminiServiceImpl
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.domain.model.usecase.SeleccionarOperacionIA
import com.wezen.tiempopormates.domain.model.usecase.corregirComprensionCase


class ComprensionViewModelFactory(
    private val dbHelper: DatabaseHelper,
    ) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ComprensionViewModel::class.java -> {
                val repository = UserRepository(dbHelper)
                val corregirComprensionCase = corregirComprensionCase(dbHelper)

                ComprensionViewModel(
                    repository,
                    corregirComprensionCase,
                    seleccionarOperacionIA = SeleccionarOperacionIA()
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}