package com.wezen.tiempopormates.data.entity

data class EstadisticaTabla(
    val tabla: Int,
    var realizados: Int,
    var fallos: Int,
    val porcenrajeAciertos: Double
)

data class EstadisticaGeneral(
    val ejerciciosTotales: Int,
    var fallosTotales: Int,
    val porcentajeTotal: Double
)