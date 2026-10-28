package com.wezen.tiempopormates.domain.model.usecase

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.User
import com.wezen.tiempopormates.data.repository.UserRepository
import com.wezen.tiempopormates.data.Database.DatabaseContract
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario

private const val TAG = "MultiplicarUses"

data class NumerosMultiplicacion(
    val multiplicando: Int,
    val multiplicador: Int,
    val resultado: Int
)
data class ResultadoCorreccionMultiplicacion(
    val todosCorrectos: Boolean,
    val erroresResultado: List<Boolean>,
    val erroresCifrasIntermedias: List<Boolean>
)
class GenerarNumerosMultiplicacionUseCase(
    private val dbHelper: DatabaseHelper,
) {

    operator fun invoke(user: User,configUser: ConfiguracionUsuario): NumerosMultiplicacion {
        val digitos1 = mutableListOf<Int>()
        val digitos2 = mutableListOf<Int>()

        //Obtenemos la matriz de errores del usuario para la suma
        val arrayErroresUsuario = dbHelper.contarErroresUsuario(user.id, "multiplicar")

        val generadorNum = GenerarNumAleatorioPonderado()


        // GENERAR DÍGITOS PARA LA MULTIPLICACIÓN
        repeat(configUser.numeroCifrasMultiplicando) {
            val d1 = generadorNum(arrayErroresUsuario, 9, 1)
            digitos1.add(d1)
        }

        repeat(configUser.numeroCifrasMultiplicador) {
            val d2 = generadorNum(arrayErroresUsuario, 9, 1)
            digitos2.add(d2)
        }

        // Convertimos listas de dígitos en números
        val num1 = digitos1.joinToString("").toInt()
        val num2 = digitos2.joinToString("").toInt()
        val resultado = num1 * num2

        Log.d(TAG, "Los números son $num1 y $num2 el resultado es: $resultado")

        return NumerosMultiplicacion(num1, num2, resultado)
    }

    // Función auxiliar para generar números aleatorios ponderados
    private fun NumAleatorioPonderado(
        user: User,
        max: Int = 9,
        operacion: String
    ): Int {
        // Tu lógica de NumAleatorioPonderado original aquí
        return (1..max).random()
    }
}

class CorregirMultiplicacionUseCase(private val dbHelper: DatabaseHelper) {

    operator fun invoke(
        valoresRespuesta: List<String>,
        valoresIntermedios: List<String>,
        multiplicando: String,
        multiplicador: String,
        resultadoCorrecto: Int,
        user: User,
        configUser: ConfiguracionUsuario

    ): ResultadoCorreccionMultiplicacion {

        val resultadoStr = resultadoCorrecto.toString().padStart(valoresRespuesta.size, '0')
        Log.d(TAG, "Los números son $multiplicando y $multiplicador el resultado es: $resultadoStr")
        Log.d(TAG, "valores del usuario introducidos $valoresRespuesta")

        // Lista para marcar qué casillas tienen error
        val erroresResultado = MutableList(valoresRespuesta.size) { false }
        val erroresCifrasIntermedias = MutableList(valoresIntermedios.size) { false }

        var StringValoresIntermedios = ""

        repeat(multiplicador.length) { index ->
            val num1 = multiplicador[multiplicador.length - 1 - index].toString().toInt()
            var multParcial = num1 * multiplicando.toInt()
            var multParcialString= multParcial.toString().padStart(multiplicando.length+1, '0')
            Log.d(TAG, "Resultados intermedios: $num1 * $multiplicando = $multParcialString")
            //Completo la matrix de valoresIntermedios

            StringValoresIntermedios+=multParcialString

        }
        Log.d(TAG, "Matriz final: $StringValoresIntermedios")

        var todosCorrectos = true


        //CONFIRMO LOS RESULTADOS INTERMEDIOS

        Log.d(TAG, "Tamaño Matriz ValoreIntermedios: ${valoresIntermedios.size}")
        valoresIntermedios.forEachIndexed { index, valorIntermedioUsuario ->

            val digitoCorrecto = StringValoresIntermedios.getOrNull(index)?.toString() ?: "0"
            Log.d(TAG, "Index $index → Usuario='$valorIntermedioUsuario' | Correcto='$digitoCorrecto'")

            // Caso especial: primer dígito opcional
            if (index == 0 && valorIntermedioUsuario.isEmpty() && digitoCorrecto == "0") {
                return@forEachIndexed
            }

            // Si está vacío o incorrecto → error
            if (valorIntermedioUsuario.isEmpty() || valorIntermedioUsuario != digitoCorrecto) {

                erroresCifrasIntermedias[index] = true

            }

        }

        //CONFIRMO SOLO EL RESULTADO
        Log.d(TAG, "CORREGIMOS EL RESULTADO FINAL")
        valoresRespuesta.forEachIndexed { index, valorUsuario ->

            val digitoCorrecto = resultadoStr.getOrNull(index)?.toString() ?: "0"

            Log.d(TAG, "Index $index → Usuario='$valorUsuario' | Correcto='$digitoCorrecto'")

            // Caso especial: primer dígito opcional
            if (index == 0 && valorUsuario.isEmpty() && digitoCorrecto == "0") {
                return@forEachIndexed
            }

            // Si está vacío o incorrecto → error
            if (valorUsuario.isEmpty() || valorUsuario != digitoCorrecto) {

                erroresResultado[index] = true
                todosCorrectos = false

            }

        }
        Log.d(TAG, "Lista de errores Intermedios: ${erroresCifrasIntermedias}")
        Log.d(TAG, "Lista de errores: ${erroresResultado}")

        // ====================================================ESTADÍSTICAS=============================================================
        if (todosCorrectos) {
            //Actualizamos el tiempo del usuario
            val updates_usuario = mapOf(
                DatabaseContract.UsuariosTable.COLUMN_TIEMPO to configUser.tiempoExtraMultiplicacion
            )
            dbHelper.updateUser(user.id, updates_usuario)
            //Actualizamos el las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to +1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "multiplicar",updates_estadisticas)
        } else {
            //Actualizamos el las estadisticas
            val updates_estadisticas = mapOf(
                DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS to +1,
                DatabaseContract.DatosModulosTable.COLUMN_FALLOS to +1
            )
            dbHelper.updateUserEstadisticasModulo(user.id, "multiplicar",updates_estadisticas)
        }

        Log.d(TAG, "Se ha completado correctamente la multiplicación: ${todosCorrectos}")

        return ResultadoCorreccionMultiplicacion(todosCorrectos, erroresResultado, erroresCifrasIntermedias)
    }
}