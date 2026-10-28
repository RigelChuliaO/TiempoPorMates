package com.wezen.tiempopormates.data.entity

import androidx.compose.ui.graphics.Color

data class Module(
    val nombre: String,
    val description: String,
    val icon: String,
    val route: String,
    val color: Color

)

fun getModules(): List<Module> {
    return listOf(
        Module(
            nombre = "Sumas ",
            description = "Aprende a sumar números",
            icon = "➕",
            route = "suma",
            color =Color(0xFFEFECCB).copy(alpha = 0.8f)
        ),
        Module(
            nombre = "Restas",
            description = "Practica las restas",
            icon = "➖",
            route = "resta",
            color = Color(0xFFFF8A80).copy(alpha = 0.8f)
        ),
        Module(
            nombre = "Multiplicar",
            description = "Multiplica como un experto",
            icon = "✖️",
            route = "multiplicar",
            color = Color(0xFF80D8FF).copy(alpha = 0.8f)
        ),
        Module(
            nombre = "Dividir",
            description = "Divide y aprende",
            icon = "➗",
            route = "dividir",
            color = Color(0xFFCCFF90).copy(alpha = 0.8f)
        ),
        Module(
            nombre = "Tablas",
            description = "Memoriza las tablas",
            icon = "📋",
            route = "tablas",
            color = Color(0xFFFFD180).copy(alpha = 0.8f)
        ),
        Module(
            nombre = "Comprensión",
            description = "Resuelve problemas",
            icon = "🧠",
            route = "comprension",
            color = Color(0xFFE0B0FF).copy(alpha = 0.8f)
        ),
        Module(
            nombre = "Examen",
            description = "Pon a prueba tus conocimientos",
            icon = "📝",
            route = "examen",
            color = Color(0xFF84FFFF).copy(alpha = 0.8f)

        )
    )
}
