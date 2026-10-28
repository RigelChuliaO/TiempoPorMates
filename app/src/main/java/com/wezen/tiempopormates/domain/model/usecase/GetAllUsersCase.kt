package com.wezen.tiempopormates.domain.usecase

import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository

class GetAllUsersCase(private val userRepository: UserRepository) {

    operator fun invoke(): List<User> {
        return userRepository.getAllUsers()
    }
}