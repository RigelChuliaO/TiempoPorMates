package com.wezen.tiempopormates.data.Gemini

import android.util.Log

private const val TAG = "Gemini"

/**
 * Variables globales para guardar historias precargadas
 * Se pueden acceder desde cualquier parte de la app en cuallquier momento
 */
object HistoriasCache {
    var historia1: HistoriaComprension? = null
    var historia2: HistoriaComprension? = null

    /**
     * Las funciones siguientese utilizan para eliminar las historias una vez esten en pantalla
     */

    fun historia1Null()
    {
        Log.e(TAG,"Eliminando historia1")
        historia1 = null
    }

    fun historia2Null()
    {
        Log.e(TAG,"Eliminando historia2")
        historia2 = null
    }
}

