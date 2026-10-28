package com.wezen.tiempopormates.data.entity

import java.text.SimpleDateFormat
import java.util.Locale

data class Examen(
    val id: Long,
    val usuarioId: Long,
    val modulos: String,
    val fecha: Long,
    val aciertos: Int,
    val fallos: Int,
    val tiempo: Long

){
    fun fechaFormateada(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(fecha)
    }
}
data class PruebaExamen(
    val id: Int,
    val modulo: String, // "suma", "resta", "multiplicacion"....
    var respondida: Boolean = false,
    var esCorrecto: Boolean = false
)
