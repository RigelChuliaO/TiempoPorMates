package com.wezen.tiempopormates.ui.theme.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wezen.tiempopormates.data.Database.DatabaseHelper

class TPMNavViewModel(private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    @Suppress()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            TPMNavViewModel::class.java -> TPMNavViewModel(dbHelper) as T
            // Aquí agregar otros ViewModels
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}