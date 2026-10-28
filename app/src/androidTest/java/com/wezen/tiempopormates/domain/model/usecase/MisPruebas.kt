package com.wezen.tiempopormates.domain.model.usecase

import org.junit.Before
import org.junit.Test

class GenerarNumAleatorioPonderadoTest {

    private lateinit var generarNumAleatorio: GenerarNumAleatorioPonderado

    @Before
    fun setUp() {
        generarNumAleatorio = GenerarNumAleatorioPonderado()
    }

    /**
     * TEST PERSONALIZADO - CAMBIA ESTOS VALORES
     */
    @Test
    fun testPersonalizado() {
        // ========== CAMBIA ESTOS VALORES ==========
        val operacion = "suma"
        val maximo = 9
        val iteraciones = 20

        // Array de errores: [errores_número_0, errores_número_1, ..., errores_número_9]
        // Ejemplo: [0, 5, 1, 3, 2, 4, 1, 2, 3, 1]
        // Número 2 tiene 1 error (MÁS PROBABLE)
        // Número 1 tiene 5 errores (MENOS PROBABLE)
        val matrizErrores = intArrayOf(0, 5, 1, 3, 2, 4, 1, 2, 3, 1).toTypedArray()
        // ==========================================

        ejecutarPrueba(operacion, maximo, iteraciones, matrizErrores)
    }

    @Test
    fun testSuma() {
        val matrizErrores = intArrayOf(0, 3, 2, 1, 4, 2, 3, 1, 2, 3).toTypedArray()
        ejecutarPrueba("suma", 9, 20, matrizErrores)
    }

    @Test
    fun testResta() {
        val matrizErrores = intArrayOf(0, 5, 2, 1, 4, 2, 3, 2, 1, 3).toTypedArray()
        ejecutarPrueba("resta", 9, 20, matrizErrores)
    }

    @Test
    fun testMultiplicacion() {
        val matrizErrores = intArrayOf(0, 3, 5, 2, 1, 4, 3, 2, 5, 2).toTypedArray()
        ejecutarPrueba("multiplicacion", 9, 20, matrizErrores)
    }

    @Test
    fun testConMaximo5() {
        val matrizErrores = intArrayOf(0, 2, 1, 3, 1, 2).toTypedArray()
        ejecutarPrueba("suma", 5, 20, matrizErrores)
    }

    @Test
    fun testConMaximo3() {
        val matrizErrores = intArrayOf(0, 2, 1, 3).toTypedArray()
        ejecutarPrueba("resta", 3, 20, matrizErrores)
    }

    /**
     * Test con números que tienen 0 errores (deben igualarse)
     */
    @Test
    fun testConCerosEnErrores() {
        val matrizErrores = intArrayOf(0, 0, 3, 0, 2, 4, 0, 2, 3, 1).toTypedArray()
        ejecutarPrueba("suma", 9, 20, matrizErrores)
    }

    /**
     * Test donde un número tiene muchos menos errores
     */
    @Test
    fun testNumeroMuyProbable() {
        // El número 5 tiene 1 error, los demás tienen 10
        val matrizErrores = intArrayOf(0, 10, 10, 10, 10, 1, 10, 10, 10, 10).toTypedArray()
        ejecutarPrueba("suma", 9, 50, matrizErrores)
    }

    // ============ FUNCIÓN AUXILIAR ============
    private fun ejecutarPrueba(
        operacion: String,
        maximo: Int,
        iteraciones: Int,
        matrizErrores: Array<Int>
    ) {
        println("\n" + "=".repeat(70))
        println("TEST: $operacion | Máximo: $maximo")
        println("=".repeat(70))

        // 1. Mostrar matriz de entrada
        println("1. MATRIZ DE ENTRADA:")
        print("   [")
        (0..maximo).forEach { numero ->
            print("${matrizErrores[numero]}")
            if (numero < maximo) print(", ")
        }
        println("]")

        // 2. Calcular conteoMinimo
        var conteoMinimo = matrizErrores[0]
        for (n in 1..maximo) {
            if (matrizErrores[n] < conteoMinimo) {
                conteoMinimo = matrizErrores[n]
            }
        }
        if (conteoMinimo == 0) conteoMinimo = 2

        println("\n2. CONTEO MÍNIMO: $conteoMinimo")

        // 3. Calcular y mostrar matriz ponderada
        val matrizPonderada = matrizErrores.copyOf()
        for (n in 0..maximo) {
            if (matrizPonderada[n] == 0) {
                matrizPonderada[n] = conteoMinimo
            }
        }

        println("\n3. MATRIZ PONDERADA:")
        print("   [")
        (0..maximo).forEach { numero ->
            print("${matrizPonderada[numero]}")
            if (numero < maximo) print(", ")
        }
        println("]")

        // 4. Ejecutar 10 pruebas
        println("\n4. PRUEBAS DE NÚMEROS ALEATORIOS (10 intentos):")
        println("-".repeat(70))

        repeat(10) { i ->
            val resultado = generarNumAleatorio.invoke(matrizErrores, maximo)
            println("   Intento ${(i + 1).toString().padStart(2)}: Número generado = $resultado")
        }

        println("=".repeat(70) + "\n")
    }
}