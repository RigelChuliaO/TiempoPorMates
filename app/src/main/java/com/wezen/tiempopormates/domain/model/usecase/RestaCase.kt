package com.wezen.tiempopormates.domain.model.usecase

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseContract
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.ResultadoCorreccion
import com.wezen.tiempopormates.data.entity.User


private const val TAG = "RestaCases"


class NumerosResta(
    val minuendo: Int,
    val sustraendo: Int,
    val resultado: Int
)

class ResultadoCorreccionResta(
    val todosCorrectos: Boolean,
    val errores: List<Boolean>
)
class GenerarNumerosRestaUseCase(
    private val dbHelper: DatabaseHelper,

) {

    operator fun invoke(user: User, configUser: ConfiguracionUsuario): NumerosResta {

        val digitos1 = mutableListOf<Int>()
        val digitos2 = mutableListOf<Int>()
        var resultado: Int

        //Obtenemos la matriz de errores del usuario para la suma
        val arrayErroresUsuario = dbHelper.contarErroresUsuario(user.id, "resta")

        val generadorNum = GenerarNumAleatorioPonderado()

        repeat(configUser.numeroCifrasResta) {
            var d1: Int
            var d2: Int

            d1 = generadorNum(arrayErroresUsuario,9)
            if (configUser.llevadasResta) {
                // cualquier dígito 0–9
                d2= generadorNum(arrayErroresUsuario,9)
            } else {
                // sin llevada -> Resta de dígitos < 10

                d2 = generadorNum(arrayErroresUsuario,9-d1)
            }
            //Para asegurar que el muniendo  es mayor que el sustraendo.
            d1=maxOf(d2,d1)
            d2=minOf(d2,d1)

            digitos1.add(d1)
            digitos2.add(d2)

        }

        // Convertimos listas de dígitos en números
        val num1 = digitos1.joinToString("").toInt()
        val num2 = digitos2.joinToString("").toInt()
        resultado = num1 - num2
        Log.d(TAG, "Los numero son $num1 y $num2 el resultado es: $resultado ")

        return NumerosResta(
            num1,
            num2,
            num1 - num2
        )
    }

}
class CorregirRestaUseCase(
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
                    operacion = "resta"
                )
            }

        }
        Log.d(TAG, "Los numero son $num1 y $num2 el resultado es: $resultadoCorrecto")
        Log.d(TAG, "Lista de errores: ${errores}")
        Log.d(TAG, "Valores Usuario : ${valores}")
        if (todosCorrectos) {
            //Actualizamos el tiempo del usuario
            val updates_usuario = mapOf(
                DatabaseContract.UsuariosTable.COLUMN_TIEMPO to configUser.tiempoExtraResta
            )
            dbHelper.updateUser(user.id, updates_usuario)
            //Actualizamos el las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to +1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "resta",updates_estadisticas)
        }else {
            //Actualizamos el las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to +1,
                DatabaseContract.DatosModulosTable.COLUMN_FALLOS to +1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "resta", updates_estadisticas)
        }
        Log.d(TAG, "Resta correcta: $todosCorrectos")

        return ResultadoCorreccion(todosCorrectos, errores)
    }
}