package com.wezen.tiempopormates.domain.model.usecase

class ValidarContraseñaUserCase  {

    operator fun invoke(contraseñaIntroducido: String, contraseñaCorrecta: String?): Boolean {
        return contraseñaIntroducido == contraseñaCorrecta
    }
}