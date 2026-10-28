
package com.wezen.tiempopormates.domain.model.usecase

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseContract
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario

private const val TAG = "ComprensionUsesCases"

class corregirComprensionCase(
    private val dbHelper: DatabaseHelper
)
{
    operator fun invoke(
        respuestaUsuario: String,
        solucion: String?,
        configUser: ConfiguracionUsuario
    ): Boolean{

        Log.e(TAG, "Respuesta Usuario: $respuestaUsuario : Respuesta solucion: $solucion")

        if(respuestaUsuario!=solucion){

            //Actualizamos las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to 1,
                DatabaseContract.DatosModulosTable.COLUMN_FALLOS to 1
            )
            dbHelper.updateUserEstadisticasModulo(configUser.usuarioId, "comprension",updates_estadisticas)
            Log.e(TAG, "La Solucion es: true")


            return false
        }else{

            //Actualizamos el tiempo del usuario
            val updates_usuario = mapOf(
                DatabaseContract.UsuariosTable.COLUMN_TIEMPO to configUser.tiempoExtraComprension
            )
            dbHelper.updateUser(configUser.usuarioId, updates_usuario)
            //Actualizamos las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to +1
            )
            dbHelper.updateUserEstadisticasModulo(configUser.usuarioId, "comprension",updates_estadisticas)
            Log.e(TAG, "La Solucion es: false")
            return true
        }

    }

}

class SeleccionarOperacionIA(

)
{
    operator fun invoke(listaOperaciones: String): String{

        var operacionIA=  listaOperaciones.split(",").map { it.trim() }.random()

        Log.e(TAG, "Tipo Operacion Selecciona Historia: ")

        return operacionIA
    }
}


