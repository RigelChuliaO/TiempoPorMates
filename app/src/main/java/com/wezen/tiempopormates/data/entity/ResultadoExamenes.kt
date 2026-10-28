package com.wezen.tiempopormates.data.entity

data class ResultadosExamen(
    val totalPruebas: Int,
    val pruebasCorrectas: Int,
    val pruebasIncorrectas: Int,
    val porcentajeAcierto: Int
)