package com.wezen.tiempopormates.data.entity

import android.util.Log

data class DatosUsuarioModulo (

    val usuarioId: Long = 0,
    val sumaRealizados: Int = 0,
    val sumaFallos: Int = 0,
    val restaRealizados: Int = 0,
    val restaFallos: Int = 0,
    val multiplicacionRealizados: Int = 0,
    val multiplicacionFallos: Int = 0,
    val divisionRealizados: Int = 0,
    val divisionFallos: Int = 0,
    val comprensionRealizados: Int = 0,
    val comprensionFallos: Int = 0,

    )
     {

     val porcentajeSuma: Double
         get() = porcentaje(sumaRealizados, sumaFallos)

     val porcentajeResta: Double
         get() = porcentaje(restaRealizados, restaFallos)

     val porcentajeMultiplicacion: Double
         get() = porcentaje(multiplicacionRealizados, multiplicacionFallos)

     val porcentajeDivision: Double
         get() = porcentaje(divisionRealizados, divisionFallos)

     val porcentajeComprension: Double
         get() = porcentaje(comprensionRealizados, comprensionFallos)

     private val porcentajes: Map<String, Double>
         get() = mapOf(
             "Sumas" to porcentajeSuma,
             "Restas" to porcentajeResta,
             "Multiplicaciones" to porcentajeMultiplicacion,
             "Divisiones" to porcentajeDivision,
             "Comprensión" to porcentajeComprension
         )

     /* ---------- MEJOR / PEOR ---------- */
     val mejorArea: String
         get() = porcentajes.maxByOrNull { it.value }?.key ?: ""

     val peorArea: String
         get() = porcentajes.minByOrNull { it.value }?.key ?: ""

     /* ---------- TOTALES ---------- */
     val totalRealizados: Int
         get() = sumaRealizados + restaRealizados +
                 multiplicacionRealizados + divisionRealizados +
                 comprensionRealizados

     val totalFallos: Int
         get() = sumaFallos + restaFallos +
                 multiplicacionFallos + divisionFallos +
                 comprensionFallos

     val porcentajeTotal: Double
         get() =
             if (totalRealizados > 0)
                 (totalRealizados - totalFallos) * 100.0 / totalRealizados
             else 0.0


}
fun porcentaje(realizados: Int, fallos: Int): Double {

    if (realizados > 0) {
        Log.e("DatosUsuario", "Realizados: ${realizados}")
        Log.e("DatosUsuario", "Fallos: ${fallos}")

        return (realizados - fallos) * 100.0 / (realizados)
    }else
        return 0.0
}
