package com.wezen.tiempopormates.domain.model.usecase

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseContract
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import kotlin.collections.mapOf


private const val TAG = "GuardarConfiguracionUseCase"

class GuardarConfiguracionUseCase(private val dbHelper: DatabaseHelper) {

    operator fun invoke(
        userId: Long,
        configuracion: ConfiguracionUsuario
    ): Boolean {

        val updates = mapOf(
            DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_SUMA to configuracion.numeroCifrasSuma.toString(),
            DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_SUMA to configuracion.tiempoExtraSuma.toString(),
            DatabaseContract.ConfigTable.COLUMN_LLEVAS_SUMA to if (configuracion.llevadasSuma) "1" else "0",

            DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_RESTA to configuracion.numeroCifrasResta.toString(),
            DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_RESTA to configuracion.tiempoExtraResta.toString(),
            DatabaseContract.ConfigTable.COLUMN_LLEVAS_RESTA to if (configuracion.llevadasResta) "1" else "0",

            DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICANDO to configuracion.numeroCifrasMultiplicando.toString(),
            DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICADOR to configuracion.numeroCifrasMultiplicador.toString(),
            DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_MULTIPLICACION to configuracion.tiempoExtraMultiplicacion.toString(),

            DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_DIVIDENDO to configuracion.numeroCifrasDividendo.toString(),
            DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_DIVISOR to configuracion.numeroCifrasDivisor.toString(),
            DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_DIVISION to configuracion.tiempoExtraDivision.toString(),
            DatabaseContract.ConfigTable.COLUMN_SOLO_EXACTAS_DIVISION to if (configuracion.soloExactasDivision) "1" else "0",

            DatabaseContract.ConfigTable.COLUMN_NUMERO_PREGUNTAS_EXAMEN to configuracion.numeroPreguntasExamen.toString(),
            DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_EXAMEN to configuracion.tiempoExtraExamen.toString(),
            DatabaseContract.ConfigTable.COLUMN_LISTADO_MODULOS_EXAMEN to configuracion.listadoModulosExamen.toString(),

            DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_COMPRENSION to configuracion.tiempoExtraComprension.toString(),
            DatabaseContract.ConfigTable.COLUMN_EJERCICIOS_COMPRENSION to configuracion.ejerciciosComprension.toString(),

            DatabaseContract.ConfigTable.COLUMN_TABLAS_MULTIPLICAR to configuracion.tablasMultiplicar.toString(),
            DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_TABLAS_MULTIPLICAR to configuracion.tiempoExtratablasMultiplicar.toString()
        )

        return try {
            dbHelper.guardaConfiguracion(
                usuarioId = userId,
                updates
            )
            Log.d(TAG, "Se han actualizado las configuraciones")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error guardando configuración", e)
            false
        }
    }
}

class ResetearTiempoUseCase(private val dbHelper: DatabaseHelper) {

    operator fun invoke(userId: Long, tiempoActual: Int): Boolean {
        return try {
            // La operación es completamente correcta
            dbHelper.updateUser(
                usuarioId = userId,
                updates = mapOf(
                    DatabaseContract.UsuariosTable.COLUMN_TIEMPO to -tiempoActual, // Restar todo el tiempo
                )
            )
            Log.d(TAG, "Se ha borrado todo el tiempo del usuario")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error reseteando tiempo", e)
            false
        }
    }
}