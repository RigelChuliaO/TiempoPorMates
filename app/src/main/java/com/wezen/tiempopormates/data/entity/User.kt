package com.wezen.tiempopormates.data.entity
import java.util.Date

data class User(
    val id: Long,
    val nombre: String,
    val avatar: String?,
    val tiempo: Int= 0,
    val fechaCreacion: Date?= Date(),

) {
    constructor(nombre: String) : this(
        id =0,
        nombre = nombre,
        avatar = null,
        tiempo = 0,
        fechaCreacion = Date(),

    )

}