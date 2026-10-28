package com.wezen.tiempopormates.domain.model.usecase

import com.wezen.tiempopormates.data.entity.EstadisticaGeneral
import com.wezen.tiempopormates.data.entity.EstadisticaTabla



private const val TAG = "EstadisticasUsesCases"

fun CalculoEstadisticasGeneralesTablasUser(estadisticas: Map<Int, EstadisticaTabla>): EstadisticaGeneral {

    val ejerciciosTotalesTablas = estadisticas.values.sumOf { it.realizados }
    val totalFallosTablas = estadisticas.values.sumOf { it.fallos }


    val porcentajeTotal = if (ejerciciosTotalesTablas > 0) {
        ((ejerciciosTotalesTablas - totalFallosTablas).toDouble() / ejerciciosTotalesTablas * 100)
    } else 0.0

    return EstadisticaGeneral(ejerciciosTotalesTablas,totalFallosTablas, porcentajeTotal)
}
fun CalculoMejorYPeorTabla(estadisticas: Map<Int, EstadisticaTabla>): Pair<String, String> {
    if (estadisticas.isEmpty()) {
        return Pair("N/A", "N/A")
    }

    val estadisticasConEjercicios = estadisticas.filterValues { it.realizados > 0 }

    if (estadisticasConEjercicios.isEmpty()) {
        return Pair("N/A", "N/A")
    }

    val maxPorcentaje = estadisticasConEjercicios.maxOfOrNull { it.value.porcenrajeAciertos } ?: 0.0
    val minPorcentaje = estadisticas.minOfOrNull { it.value.porcenrajeAciertos } ?: 0.0

    val mejores = estadisticasConEjercicios
        .filterValues { it.porcenrajeAciertos == maxPorcentaje }
        .keys.sorted()

    val peores = estadisticas
        .filterValues { it.porcenrajeAciertos == minPorcentaje }
        .keys.sorted()

    val mejoresTexto = if (mejores.size == 1)
        "${mejores.first()}"
    else
        "${mejores.joinToString("-")}"

    val peoresTexto = if (peores.size == 1)
        "${peores.first()}"
    else
        "${peores.joinToString("-")}"

    return Pair(mejoresTexto, peoresTexto)
}
/*
fun CalculoEstadisticasGeneralesTablasUser(estadisticas: Map<Int, EstadisticaTabla>): EstadisticaGeneral {

    val ejerciciosTotales = estadisticas.values.sumOf { it.realizados }
    val totalFallosTablas = estadisticas.values.sumOf { it.fallos }

    val ejerciciosTotales = user.ejerciciosSuma + user.ejerciciosResta +
            user.ejerciciosMultiplicacion + user.ejerciciosDivision + totalRealizadosTablas

    val fallosTotales = user.fallosSuma + user.fallosResta +
            user.fallosMultiplicacion + user.fallosDivision + totalFallosTablas

    val porcentajeTotal = if (ejerciciosTotales > 0) {
        ((ejerciciosTotales - fallosTotales).toDouble() / ejerciciosTotales * 100)
    } else 0.0

    return EstadisticaGeneral(ejerciciosTotales,fallosTotales, porcentajeTotal)
}*/