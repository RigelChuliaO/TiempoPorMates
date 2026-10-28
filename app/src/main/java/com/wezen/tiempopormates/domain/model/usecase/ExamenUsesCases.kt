package com.wezen.tiempopormates.domain.model.usecase

import androidx.media3.common.util.Log
import com.wezen.tiempopormates.data.entity.PruebaExamen



private const val TAG = "ExamenUses"

class ExamenUsesCases {

    fun generarPruebasExamen(
        numeroPruebas: Int,
        listadoModulos: String
    ): List<PruebaExamen> {

        Log.d(TAG, "Numero pruebas $numeroPruebas de las siguientes operaciones: $listadoModulos")
        val listaDeOperaciones: List<String> = listadoModulos.split(",")

        val pruebas = mutableListOf<PruebaExamen>()


        repeat(numeroPruebas) { index ->
            pruebas.add(
                PruebaExamen(
                    id = index,
                    //operacion = listaDeOperaciones[0]
                    modulo = listaDeOperaciones[index % listaDeOperaciones.size]
                )
            )
        }
        Log.d(TAG, "Pruebas generadas: $pruebas")
        return pruebas
    }
}