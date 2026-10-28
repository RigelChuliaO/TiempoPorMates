package com.wezen.tiempopormates.domain.model.usecase

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.Database.DatabaseContract
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import kotlin.random.Random



private const val TAG = "TablasUsesCases"

data class NumeroTabla(
    val tabla: Int,
    val multiplicador: Int,
    val resultado: Int
)

private val random = Random(System.currentTimeMillis())

class GenerarOperacionTabla(

){

    operator fun invoke(user: User,configUser: ConfiguracionUsuario): NumeroTabla {
        // Parseamos el string de tablas que tiene configuradas el cliente  y lo convertimos en lista de números


        val tablas = configUser.tablasMultiplicar.split(",")
            .map { it.trim().toIntOrNull() }
            .filterNotNull()
            .filter { it in 1..10 }

        // Seleccionamos aleatoriamente una tabla de la lista
        val tabla = tablas.random(random)

        // Generamos un multiplicador aleatorio entre 1 y 10
        val multiplicador = random.nextInt(1, 11)
        //TODO hay que cambiarlo para que use la ponderacion por el error


        // Calculamos el resultado
        val resultado = tabla * multiplicador

        Log.d(TAG,"Los números son $tabla y $multiplicador, el resultado es: $resultado, tablas: $tablas"
        )

        return NumeroTabla(tabla, multiplicador, resultado)
    }
}

class CorregirOperacionTablasUseCase(private val dbHelper: DatabaseHelper) {

    operator fun invoke(
        respuesta: Int,
        user: User,
        tabla: Int,
        multiplicador: Int,
        resultado: Int,
        configUser: ConfiguracionUsuario
    ): Boolean {

        Log.d(TAG, "Entrada: Resultado: ${resultado}   Respuesta:${respuesta}")
        var Correcta = true

        if (respuesta == resultado) {
            Correcta = true
        } else {
            Correcta = false
        }

        // ====================================================ESTADÍSTICAS=============================================================
        if (Correcta) {
            // La operación es completamente correcta actualizamos la tabla del usuario con el acierto
            // TODO estadísticas de las tablas de multiplicar confirmar la tabla
            // Añado las estadísticas a la tabla que corresponde
            dbHelper.updateUserStatisticsTablas(
                usuarioId = user.id,
                tabla = tabla,
                updates = mapOf(
                    DatabaseContract.EstadisticasTablas.COLUMN_REALIZADOS to 1, // +1 a ejercicio resuelto de la tabla
                )
            )

            // Añado el tiempo por adivinar el resultado de la tabla
            val updates_usuario = mapOf(
                DatabaseContract.UsuariosTable.COLUMN_TIEMPO to configUser.tiempoExtratablasMultiplicar
            )
            dbHelper.updateUser(user.id, updates_usuario)

            Log.d(TAG, "Resultado: ${Correcta}")
        } else {
            // La operación no es correcta
            // TODO estadísticas de las tablas de multiplicar confirmar la tabla en caso de error es necesario que marque el error en las dos tablas correspondientes, no solo en una

            // Añado las estadísticas a la tabla que corresponde
            dbHelper.updateUserStatisticsTablas(
                usuarioId = user.id,
                tabla = tabla,
                updates = mapOf(
                    DatabaseContract.EstadisticasTablas.COLUMN_FALLOS to 1, // Añade un fallo
                    DatabaseContract.EstadisticasTablas.COLUMN_REALIZADOS to 1
                )
            )
            Log.d(TAG, "Resultado incorrecto: ${Correcta}")
        }

        return Correcta
    }
}
