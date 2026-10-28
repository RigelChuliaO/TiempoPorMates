package com.wezen.tiempopormates.domain.model.usecase


import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseContract
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.NumerosSuma
import com.wezen.tiempopormates.data.entity.ResultadoCorreccion
import com.wezen.tiempopormates.data.entity.User
import kotlin.math.max
import kotlin.math.min

private const val TAG = "SumaCase"


class GenerarNumerosSumaUseCase(
    private val dbHelper: DatabaseHelper,

) {

    operator fun invoke(user: User,configUser: ConfiguracionUsuario): NumerosSuma {

        //Obtenemos la matriz de errores del usuario para la suma
        val arrayErroresUsuario = dbHelper.contarErroresUsuario(user.id, "suma")

        Log.e(TAG, "Array de errores: ${arrayErroresUsuario}")
        Log.e(TAG, "Config Nº cifras Suma: ${configUser.numeroCifrasSuma}")
        Log.e(TAG, "Config  Llevadas?: ${configUser.llevadasSuma}")

        val generadorNum = GenerarNumAleatorioPonderado()

        val digitos1 = mutableListOf<Int>()
        val digitos2 = mutableListOf<Int>()

        repeat(configUser.numeroCifrasSuma) {

            val d1 = generadorNum(arrayErroresUsuario, 9)
            val d2 = if (configUser.llevadasSuma)
                generadorNum(arrayErroresUsuario,)
            else
                generadorNum(arrayErroresUsuario, 9 - d1)

            digitos1.add(max(d1, d2))
            digitos2.add(min(d1, d2))
        }

        Log.e(TAG, "Numeros: ${digitos1} + ${digitos2}")
        val num1 = digitos1.joinToString("").toInt()
        val num2 = digitos2.joinToString("").toInt()

        return NumerosSuma(
            num1,
            num2,
            num1 + num2
        )
    }

}
class CorregirSumaUseCase(
    private val dbHelper: DatabaseHelper
) {

    operator fun invoke(
        valores: List<String>,
        num1: String,
        num2: String,
        resultadoCorrecto: Int,
        user: User,
        configUser: ConfiguracionUsuario
    ): ResultadoCorreccion {

        val resultadoStr = resultadoCorrecto.toString().padStart(valores.size, '0')
        //Para la extraccion de las cifras en las que se ha cometido el error
        val aux_num1 = num1.padStart(num1.length + 1, '0')
        val aux_num2 = num2.padStart(num2.length + 1, '0')
        // Lista para marcar qué casillas tienen error
        val errores = MutableList(valores.size) { false }
        var todosCorrectos = true

        // Comparar cada cifra
        valores.forEachIndexed { index, valorUsuario ->

            val digitoCorrecto = resultadoStr.getOrNull(index)?.toString() ?: "0"

            // Para que no tengan que rellenar la primera de las cifras con 0. Si no es necesario
            if (index == 0 && valorUsuario.isEmpty() && digitoCorrecto=="0") {
                return@forEachIndexed
            }
            // Si el usuario no ha introducido nada o es incorrecto
            if (valorUsuario.isEmpty() || valorUsuario != digitoCorrecto) {
                //Si deja los cuadros sin rellenar no añade a las estadisticas.
                errores[index] = true
                todosCorrectos = false

                val errornum1 = aux_num1[index]
                val errornum2 = aux_num2[index]

                //.-----Añadimos cada error cometido al corregirlo
                dbHelper.añadirErroresUsuario(
                    usuarioId = user.id,
                    num1=errornum1.toString(),
                    num2=errornum2.toString(),
                    operacion = "suma"
                )
            }

        }
        Log.d(TAG, "Lista de errores: ${errores}")
        // Actualiza estadísticas del usuario
        if (todosCorrectos) {
            //Actualizamos el tiempo del usuario
            val updates_usuario = mapOf(
                DatabaseContract.UsuariosTable.COLUMN_TIEMPO to configUser.tiempoExtraSuma
            )
            dbHelper.updateUser(user.id, updates_usuario)
            //Actualizamos las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to +1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "suma",updates_estadisticas)

        }else{
            //Actualizamos las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to 1,
                DatabaseContract.DatosModulosTable.COLUMN_FALLOS to 1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "suma",updates_estadisticas)
        }


        Log.d("CorregirSumaUseCase", "Suma correcta: $todosCorrectos")

        return ResultadoCorreccion(todosCorrectos, errores)
    }
}