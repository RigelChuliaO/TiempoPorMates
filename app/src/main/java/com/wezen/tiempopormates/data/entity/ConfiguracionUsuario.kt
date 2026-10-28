package com.wezen.tiempopormates.data.entity

data class ConfiguracionUsuario(
    val id: Long = 0,
    val usuarioId: Long = 0,

    // Suma
    val numeroCifrasSuma: Int = 1,
    val tiempoExtraSuma: Int = 0,
    val llevadasSuma: Boolean = false,

    // Resta
    val numeroCifrasResta: Int = 1,
    val tiempoExtraResta: Int = 0,
    val llevadasResta: Boolean = false,

    // Multiplicación
    val numeroCifrasMultiplicando: Int = 2,
    val numeroCifrasMultiplicador: Int = 1,
    val tiempoExtraMultiplicacion: Int = 1,

    // División
    val numeroCifrasDividendo: Int = 1,
    val numeroCifrasDivisor: Int = 1,
    val tiempoExtraDivision: Int = 0,
    val soloExactasDivision: Boolean = false,

    // Tablas de multiplicar
    val tablasMultiplicar: String = "1,2,3,4,5,6,7,8,9,10",
    val tiempoExtratablasMultiplicar: Int = 0,

    // Comprensión
    val tiempoExtraComprension: Int = 0,
    val ejerciciosComprension: String = "suma,resta,multiplicacion,division",

    // Examen
    val numeroPreguntasExamen: Int = 5,
    val tiempoExtraExamen: Int = 0,
    val listadoModulosExamen: String = "SUMA,RESTA,MULTIPLICACION,DIVISION",

    // Password
    val password: String? = "1234",
)
