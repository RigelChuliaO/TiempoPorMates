package com.wezen.tiempopormates.domain.model.usecase


import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GenerarNumAleatorioPonderadoTest {

    private lateinit var generarNumAleatorio: GenerarNumAleatorioPonderado

    @Before
    fun setUp() {
        generarNumAleatorio = GenerarNumAleatorioPonderado()
    }

    @Test
    fun testConMatrizConValoresDiferentes() {
        // Matriz con valores diferentes
        val matrizErrores = arrayOf(0, 5, 3, 0, 8, 1, 0, 6, 2, 4)
        val max = 9

        println("=== TEST: Matriz con Valores Diferentes ===")
        println("Matriz de errores: ${matrizErrores.toList()}")
        println("Max: $max")

        repeat(5) { iteracion ->
            val resultado = generarNumAleatorio(matrizErrores, max)
            println("Iteración $iteracion: Número seleccionado = $resultado")
        }
        println()
    }

    @Test
    fun testDebugConSeguimientoDetallado() {
        val matrizErrores = arrayOf(0, 3, 0, 5, 0, 2, 0, 4, 1, 3)
        val max = 9

        println("=== TEST: Debug Detallado con Seguimiento ===")
        println("Matriz original: ${matrizErrores.toList()}")
        println()

        // Simulamos el algoritmo para ver todas las variables intermedias
        val limite = max.coerceIn(0, 9)
        println("1. Límite (after coerceIn): $limite")
        println()

        var totalContNumero = 0
        var conteoMinimo = matrizErrores[0]

        println("2. Primera pasada (buscar mínimo):")
        for (n in 1..limite) {
            totalContNumero += matrizErrores[n]
            if (matrizErrores[n] < conteoMinimo) {
                conteoMinimo = matrizErrores[n]
            }
            println("   n=$n: matrizErrores[$n]=${matrizErrores[n]}, conteoMinimo=$conteoMinimo, totalContNumero=$totalContNumero")
        }
        println()
        println("3. Conteo mínimo encontrado: $conteoMinimo")

        if (conteoMinimo == 0) {
            conteoMinimo = 2
            println("   Conteo mínimo era 0, se ajusta a 2")
        }
        println()

        val erroresSinCero = matrizErrores.copyOf()
        println("4. Segunda pasada (igualar probabilidades):")
        totalContNumero = 0

        for (n in 0..limite) {
            if (erroresSinCero[n] == 0) {
                erroresSinCero[n] = conteoMinimo
                println("   n=$n: era 0, se ajusta a $conteoMinimo")
            }
            totalContNumero += erroresSinCero[n]
        }
        println()
        println("5. Matriz después de igualar probabilidades (erroresSinCero):")
        println("   ${erroresSinCero.toList()}")
        println()
        println("6. Total de números (suma de probabilidades): $totalContNumero")
        println()

        // Ejecutar la función real
        val resultado = generarNumAleatorio(matrizErrores, max)
        println("7. Número seleccionado: $resultado")
        println()

        assertTrue(resultado in 1..limite)
    }
}