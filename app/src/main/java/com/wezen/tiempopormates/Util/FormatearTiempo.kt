package com.wezen.tiempopormates.Util

//Funcion que da formato en minutos segundos a los segundos que tiene el usuario
fun FormatearTiempo (segundos: Int): String {
    val horas = segundos / 3600
    val minutos = (segundos % 3600) / 60
    val segs = segundos % 60

    return when {
        horas > 0 -> "${horas}h ${minutos}m ${segs}s"
        minutos > 0 -> "${minutos}m ${segs}s"
        else -> "${segs}s"
    }
}