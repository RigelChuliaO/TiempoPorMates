package com.wezen.tiempopormates.domain.model.usecase

import com.wezen.tiempopormates.data.repository.UserRepository

class DeleteUserCase (private val userRepository: UserRepository) {

    operator fun invoke(idUser:Long):Boolean{
        return userRepository.deleteUser(idUser)
    }
}