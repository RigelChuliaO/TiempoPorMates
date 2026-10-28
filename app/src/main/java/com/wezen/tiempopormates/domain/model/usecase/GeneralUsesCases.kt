package com.wezen.tiempopormates.domain.model.usecase

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.User
import kotlin.random.Random


private const val TAG = "GeneralUsesCases"


fun NumAleatorioPonderado(user: User, dbHelper: DatabaseHelper, max: Int = 9, operacion: String): Int {
    val max = max.coerceIn(0,9)//Para que siempre sea un valor entre 0 y 9
    //Primero cargamos los errores del usuario

    //Hay un caso en el que el maximo es 0 por que el otro numero es 9. Asi que el valor aleatorio solo puede ser 0.
    //Por este motivo si el valor de entrada es 0, devolveremos siempre 0
    if (max==0) return 0

    val arrayErroresUsuario = dbHelper.contarErroresUsuario(user.id,operacion)

    var totalContNumero: Int = 0
    //Buscamos el numero que tiene la menor probabilidad
    var conteoMinimo: Int = arrayErroresUsuario[0]
    for  (n in 1.. max){
        totalContNumero=totalContNumero+arrayErroresUsuario[n]
        if (arrayErroresUsuario[n]<conteoMinimo) {
            conteoMinimo = arrayErroresUsuario[n]

        }
    }
    if (conteoMinimo==0) conteoMinimo=2
    //Añadimos  el Bono a aquellos que tienen pocas posibilidades de salir para igualar los porcentajes
    for (n in 1.. max) {
        if (arrayErroresUsuario[n] == 0) {
            arrayErroresUsuario[n] = conteoMinimo

        }
        totalContNumero = totalContNumero + arrayErroresUsuario[n]
    }
    Log.d(TAG, "Total de numeros $totalContNumero, $max")
    //Seleccionamos un numero aleatorio entre el total de muestras, Lo que viene ser elegir el numero aleatoriamente.
    val NumAleatorio = Random.Default.nextInt(totalContNumero)
    var numeroSeleccionado=1
    var sumaAcumlada = 0
    for  (n in 1.. max){
        sumaAcumlada += arrayErroresUsuario[n]
        if (NumAleatorio <= sumaAcumlada) {
            numeroSeleccionado = n
            break
        }
    }


    Log.d(TAG, "Numero Aleatorio $NumAleatorio, numero Seleccionado $numeroSeleccionado")

    return numeroSeleccionado
}

fun formatearTiempo(segundos: Int): String {
    val horas = segundos / 3600
    val minutos = (segundos % 3600) / 60
    val segs = segundos % 60

    return when {
        horas > 0 -> "${horas}h ${minutos}m ${segs}s"
        minutos > 0 -> "${minutos}m ${segs}s"
        else -> "${segs}s"
    }
}

fun obtenerNota(porcentaje: Double): String {

    if (porcentaje >= 90) {
        return "Sobresaliente"
    } else if (porcentaje >= 75) {
        return "Notable"
    } else if (porcentaje >= 60) {
        return "Bien"
        } else if (porcentaje >= 50) {
        return "Suficiente"
    } else {
        return "insuficiente"
    }

}