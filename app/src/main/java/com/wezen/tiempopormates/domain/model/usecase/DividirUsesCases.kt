package com.wezen.tiempopormates.domain.model.usecase

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseContract
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.User
import kotlin.random.Random

private const val TAG = "DividirUses"

data class DatosDivision(
    val cociente: Int,
    val resto: Int,
    val dividendo: Int,
    val divisor: Int
)

data class ResultadoDivision(
    val cociente:  Int,
    val resto: Int,
    val divisionCorrecta: Boolean,
    val erroresCociente: List<Boolean>,
    val erroresResto: List<Boolean>,
    val erroresCanvas: List<Boolean> // Para lacorrecion de las operaciones intermedias
)

fun GenerarNuevaDivision(configUser: ConfiguracionUsuario): DatosDivision {
    Log.d(TAG, "Generando Numeros")
    val dividendo_aux = mutableListOf<Int>()
    val divisor_aux = mutableListOf<Int>()
    var dividendo: Int
    var divisor: Int

    //do {  //Buqule para aseguramr que el dividendo siempre sea mayor que el divisor.

        //Calculoamos el dividendo
    repeat(configUser.numeroCifrasDividendo) {
        val aux= Random.nextInt(2,10)
        dividendo_aux.add(aux)
    }

    //Calculamos el divisor
    repeat(configUser.numeroCifrasDivisor) {
        val aux= Random.nextInt(2,10)
        divisor_aux.add(aux)
    }

    // Convertimos las dos listas a numeros
    dividendo = dividendo_aux.joinToString("").toInt()
    divisor = divisor_aux.joinToString("").toInt()
    //} while (dividendo <= divisor)

    val cociente= dividendo/divisor
    val resto = dividendo%divisor
    Log.d(TAG, "dividendo: $dividendo, divisor: $divisor, cociente: $cociente, resto: $resto")

    return DatosDivision(cociente, resto, dividendo, divisor)
}

class CorregirDivisionUseCase(private val dbHelper: DatabaseHelper){
    operator fun invoke(
        valoresResto: List<String>,
        valoresUsuarioCociente: List<String>,
        valoresUsuarioCanvas: List<String>,
        datosDivision: DatosDivision,
        user: User,
        configUser: ConfiguracionUsuario
    ): ResultadoDivision {
        Log.d(TAG, "Corrigiendo Division par el id: ${user.id}")
        Log.d(TAG, "dividendo: ${datosDivision.dividendo}, divisor: ${datosDivision.divisor}")
        Log.d(TAG, "Resto: ${datosDivision.resto}, Cociente: ${datosDivision.cociente}, valoresUsuarioCanvas: $valoresUsuarioCanvas")


        var cocienteCorrecto: Boolean = false
        var restoCorrecto: Boolean = false

        //Comprobacion del Cociente
        //Se comprueba caracter a caracter para poder señalar los que no son correctos
        val erroresCociente = mutableListOf<Boolean>()
        for (i in valoresUsuarioCociente.indices) {

            if (valoresUsuarioCociente[i] != datosDivision.cociente.toString()[i].toString()) {
                erroresCociente.add(true)
            }else{
                erroresCociente.add(false)
            }

        }

        //Comprobacion del Cociente
        val cocienteStr = valoresUsuarioCociente.joinToString("").trim()
        val cocienteUsuario = cocienteStr.toIntOrNull() ?: -1

        cocienteCorrecto = cocienteUsuario == datosDivision.cociente

        Log.d(TAG, "CocienteStr: '$cocienteStr', CocienteUsuario: $cocienteUsuario, CocienteCorrecto: $cocienteCorrecto")

        //Comprobacion del Resto
        val restoStr = obtenerResto(valoresResto, configUser.numeroCifrasDividendo).toString()
        val restoUsuario = restoStr.toIntOrNull() ?: -1

        restoCorrecto = restoUsuario == datosDivision.resto

        Log.d(TAG, "RestoStr: '$restoStr', RestoUsuario: $restoUsuario, RestoCorrecto: $restoCorrecto")



        Log.d(TAG, "ErroresCociente: $erroresCociente")

         //----------------------------------------------------ESTADISTICAS------------------------------------------------------------------------

        if (cocienteCorrecto && restoCorrecto){
            //Actualizamos el tiempo del usuario
            val updates_usuario = mapOf(
                DatabaseContract.UsuariosTable.COLUMN_TIEMPO to configUser.tiempoExtraDivision
            )
            dbHelper.updateUser(user.id, updates_usuario)
            //Actualizamos las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to 1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "dividir",updates_estadisticas)
            Log.d(TAG, "Division correcta")
        }else{

            //Actualizamos las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to 1,
                DatabaseContract.DatosModulosTable.COLUMN_FALLOS to 1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "dividir",updates_estadisticas)
            Log.d(TAG, "No es correcta la Division")
        }



        return ResultadoDivision(0,0,(cocienteCorrecto && restoCorrecto), erroresCociente, emptyList(), emptyList())
    }
}

fun primerNumeroMayorQueDivisor(dividendo: Int, divisor: Int): Int {
    val texto = dividendo.toString()
    var actual = ""

    for (c in texto) {
        actual += c
        if (actual.toInt() > divisor) {
            return actual.length
        }
    }

    return actual.length // por si todo el número es necesario
}

fun obtenerResto(
    array: List<String>,
    tamano: Int
): Int {

    Log.e(TAG, "Array: $array, Tamano: $tamano")

    if (array.isEmpty()) return -1
    if (tamano <= 0) return -1 // El tamaño de los grupos mayor que 0
    if (array.size % tamano != 0) return -1// Es necesario que el array tenga un tamaño multimo de tamaño


    val bloques = array.toList().chunked(tamano) //Lo partimos en array pequeños

    val ultimoBloqueConValor = bloques
        .lastOrNull { bloque -> bloque.any { it != null } }
        ?: return -1

    return try {
        ultimoBloqueConValor
            .filterNotNull()
            .joinToString("")
            .toInt()
    }catch (e: NumberFormatException) {
        -1
    }
}
