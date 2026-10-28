package com.wezen.tiempopormates.domain.model.usecase

import com.wezen.tiempopormates.data.repository.UserRepository

class CreateUserCase(private val userRepository: UserRepository) {

    operator fun invoke(nombre: String, avatar: String? = null): Long {
        return userRepository.createUser(nombre, avatar)
    }
}