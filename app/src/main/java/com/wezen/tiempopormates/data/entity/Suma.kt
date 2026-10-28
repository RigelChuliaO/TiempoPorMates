package com.wezen.tiempopormates.data.entity


    class NumerosSuma(
        val sumando1: Int,
        val sumando2: Int,
        val resultado: Int
    )

    class ResultadoCorreccion(
        val todosCorrectos: Boolean,
        val errores: List<Boolean>
    )
