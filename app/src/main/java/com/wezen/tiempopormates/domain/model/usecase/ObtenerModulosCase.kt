package com.wezen.tiempopormates.domain.model.usecase

import com.wezen.tiempopormates.data.entity.Module
import com.wezen.tiempopormates.data.entity.getModules

class ObtenerModulosCase {

    operator fun invoke(): List<Module> {
        return getModules()
    }
}