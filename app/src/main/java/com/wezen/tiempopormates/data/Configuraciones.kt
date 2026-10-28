package com.wezen.tiempopormates.data


data class Configuraciones (
    val usuario: Int,
    val suma: ConfiguracionSuma,
    val resta: ConfiguracionResta,
    val multiplicacion: ConfiguracionMultiplicacion,
    val division: ConfiguracionDivision,


)

data class ConfiguracionSuma(

    val cifras: Int,
    val tiempoextra:Int,
    val conllevadas: Boolean
)
data class ConfiguracionResta(

    val cifras: Int,
    val tiempoextra:Int,
    val conllevadas: Boolean
)
data class ConfiguracionMultiplicacion(

    val cifrasMultiplicando: Int,
    val cifrasMultiplicador: Int,
    val tiempoextra:Int,
    val conllevadas: Boolean
)
data class ConfiguracionDivision(

    val cifrasDividendo: Int,
    val cifrasDivisor: Int,
    val tiempoextra:Int,
    val soloExactas: Boolean
)
