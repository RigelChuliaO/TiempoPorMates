package com.wezen.tiempopormates.domain.model.usecase

import androidx.media3.common.util.Log
import kotlin.math.ceil
import kotlin.random.Random

private const val TAG = "NumAleatorioPonderado"

class GenerarNumAleatorioPonderado(

) {
    /***
     * Esta funcion elige un numero aleatorio entre 0 y max, teniendo en cuenta los errores del usuario.
     * @param matrizErrores La matriz de errores del usuario
     * @param max El numero maximo a elegir
     * @param min numero minimo a seleccionar
     * @return El numero elegido
     */
    operator fun invoke(
        matrizErrores: Array<Int>,
        max: Int = 9,
        min: Int = 1,
    ): Int {

        //Verificamos que los valores de entrada son posibles
        val matrizErrores = matrizErrores.copyOf() //Para no modificar la matriz de entada
        val limite = max.coerceIn(0, 9) // Forzamos el valor de entrada
        val limite_min = min.coerceIn(1, limite) // Forzamos el valor de entrada minimo este por debajo del maximo y sea 0

        if (limite == 0) return 0


        Log.d(TAG, "Matriz Errores: [${matrizErrores.joinToString(", ")}], numero Max: $max, numero min: $min")


        // Sumamos la media de los valores a todos los que estan por debajo
        val media = matrizErrores.slice(limite_min..limite).average()

        for (n in limite_min..limite) {
            if (matrizErrores[n] < media) {
                matrizErrores[n] = ceil(matrizErrores[n] + media).toInt()
            }
        }
        // Sustituimos los 0 para que sumen y puedan ser seleccionados
        for (i in matrizErrores.indices) {
            if (matrizErrores[i] == 0) {
                matrizErrores[i] = 1
            }
        }

        // Construyo una array para ver la proobabilidades de cada numero
        val suma = matrizErrores.sum()

        val arrayNormalizado: DoubleArray =
            if (suma > 0) {
                matrizErrores.map { it.toDouble()*100 / suma }.toDoubleArray()
            } else {
                DoubleArray(matrizErrores.size) { 0.0 }
            }

        Log.d(TAG, "Array de errores Ponderado: [${matrizErrores.joinToString(", ")}]")
        Log.d(TAG, "Array de errores Ponderado%: [${arrayNormalizado.joinToString(", ")}]")


        val numAleatorio = (0..suma).random()
        var sumaAcumulada = 0
        var numeroSeleccionado = limite_min

        for (n in limite_min..limite) {
            sumaAcumulada += matrizErrores[n]
            if (numAleatorio <= sumaAcumulada) {
                numeroSeleccionado = n
                break
            }
        }

        Log.d(TAG, "NumAleatorio = $numAleatorio de $suma → seleccionado = $numeroSeleccionado")

        return numeroSeleccionado
    }

}