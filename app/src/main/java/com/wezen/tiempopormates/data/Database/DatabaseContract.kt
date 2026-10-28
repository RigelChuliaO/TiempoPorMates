package com.wezen.tiempopormates.data.Database

import android.provider.BaseColumns

object DatabaseContract {

    // Configuración general de la base de datos
    const val DATABASE_NAME = "TiempoPorMates.db"
    const val DATABASE_VERSION = 6

    // Tabla Usuarios
    object UsuariosTable : BaseColumns {
        const val TABLE_NAME = "Usuarios"
        const val COLUMN_ID = "Id"
        const val COLUMN_NOMBRE = "Nombre"
        const val COLUMN_AVATAR = "Avatar"
        const val COLUMN_FECHA_CREACION = "fechaCreacion"

        const val COLUMN_TIEMPO = "Tiempo"   //Valor para el tiempo acumulado en segundos

    }
    object DatosModulosTable : BaseColumns {
        const val TABLE_NAME = "DatosModulosTable"
        const val COLUMN_MODULO = "Modulo"
        const val COLUMN_USUARIO_ID = "UsuarioId"
        const val COLUMN_REALIZADOS = "realizados"
        const val COLUMN_FALLOS = "fallos"
    }

    object ConfigTable : BaseColumns {
        const val TABLE_NAME = "Configuraciones"
        const val COLUMN_ID = "Id"
        const val COLUMN_USUARIO_ID = "UsuarioId"
        const val COLUMN_PASSWORD = "password"

        // Suma
        const val COLUMN_NUMERO_CIFRAS_SUMA = "numeroCifrasSuma"
        const val COLUMN_TIEMPO_EXTRA_SUMA = "tiempoExtraSuma"
        const val COLUMN_LLEVAS_SUMA = "llevadasSuma"

        // Resta
        const val COLUMN_NUMERO_CIFRAS_RESTA = "numeroCifrasResta"
        const val COLUMN_TIEMPO_EXTRA_RESTA = "tiempoExtraResta"
        const val COLUMN_LLEVAS_RESTA = "llevadasResta"

        //Multiplicacion
        const val COLUMN_NUMERO_CIFRAS_MULTIPLICANDO = "numeroCifrasMultiplicando"
        const val COLUMN_NUMERO_CIFRAS_MULTIPLICADOR = "numeroCifrasMultiplicador"
        const val COLUMN_TIEMPO_EXTRA_MULTIPLICACION = "tiempoExtraMultiplicacion"

        //Division
        const val COLUMN_NUMERO_CIFRAS_DIVIDENDO = "numeroCifrasDividendo"
        const val COLUMN_NUMERO_CIFRAS_DIVISOR = "numeroCifrasDivisor"
        const val COLUMN_TIEMPO_EXTRA_DIVISION = "tiempoExtraDivision"
        const val COLUMN_SOLO_EXACTAS_DIVISION = "soloExactasDivision"

        // Tablas de multiplicar
        const val COLUMN_TABLAS_MULTIPLICAR = "tablasMultiplicar"
        const val COLUMN_TIEMPO_EXTRA_TABLAS_MULTIPLICAR = "tiempoExtraTablasMultiplicar"

        // Comprension
        const val COLUMN_TIEMPO_EXTRA_COMPRENSION = "tiempoExtraComprension"
        const val COLUMN_EJERCICIOS_COMPRENSION = "ejerciciosComprension"

        // Examenes
        const val COLUMN_NUMERO_PREGUNTAS_EXAMEN = "numeroPreguntasExamen"
        const val COLUMN_TIEMPO_EXTRA_EXAMEN = "tiempoExtraExamen"
        const val COLUMN_LISTADO_MODULOS_EXAMEN = "listadoModulosExamen"
    }

    // Tabla Pruebas
    object PruebasTable : BaseColumns {
        const val TABLE_NAME = "Pruebas"
        const val COLUMN_ID = "Id"
        const val COLUMN_USUARIO = "usuario"
        const val COLUMN_FECHA = "Fecha"
        const val COLUMN_ACIERTOS = "Aciertos"
        const val COLUMN_FALLOS = "Fallos"
        const val COLUMN_MODULO = "Modulo"
        const val COLUMN_EXTRA_TOTAL = "tiempoExtra"
    }

    // Tabla Errores para poder saber donde se equivoca mas el usuario
    object ErroresTable : BaseColumns {
        const val TABLE_NAME = "Errores"
        const val COLUMN_ID = "Id"
        const val COLUMN_USUARIO = "Usuario"
        const val COLUMN_OPERACION = "Operacion"
        const val COLUMN_NUM1 = "Num1"
        const val COLUMN_NUM2 = "Num2"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_RESPUESTA_CORRECTA = "respuestaCorrecta"
        const val COLUMN_RESPUESTA_USUARIO = "respuestaUsuario"
    }

    //Tabla para guardar las estadisiticas de las tablas de multiplicar de cada usuario
    object EstadisticasTablas : BaseColumns {
        const val TABLE_NAME = "EstadisticasTablas"
        const val COLUMN_ID = "Id"
        const val COLUMN_USUARIO = "UsuarioId"
        const val COLUMN_TABLA = "Tabla"                 // Ej: 2, 3, 4...
        const val COLUMN_REALIZADOS = "Realizados"
        const val COLUMN_FALLOS = "Fallos"
        const val COLUMN_ULTIMA_FECHA = "UltimaFecha"
    }

    // Consultas de SQL para crear las disitintas tablas de la base de datos
    const val SQL_CREATE_USUARIOS = """
        CREATE TABLE ${UsuariosTable.TABLE_NAME} (
            ${UsuariosTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${UsuariosTable.COLUMN_FECHA_CREACION} DATETIME DEFAULT CURRENT_TIMESTAMP,
            ${UsuariosTable.COLUMN_NOMBRE} TEXT NOT NULL,
            ${UsuariosTable.COLUMN_AVATAR} TEXT,
            ${UsuariosTable.COLUMN_TIEMPO} INTEGER DEFAULT 0
            )
            """

    const val SQL_CREATE_DATOSMODULOS = """
        CREATE TABLE ${DatosModulosTable.TABLE_NAME} (
            ${DatosModulosTable.COLUMN_MODULO} TEXT NOT NULL,
            ${DatosModulosTable.COLUMN_USUARIO_ID} TEXT NOT NULL,
            ${DatosModulosTable.COLUMN_REALIZADOS} INTEGER DEFAULT 0,
            ${DatosModulosTable.COLUMN_FALLOS} INTEGER DEFAULT 0
            )
            """

    const val SQL_CREATE_CONFIGURACIONES = """
        CREATE TABLE ${ConfigTable.TABLE_NAME} (
            ${ConfigTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${ConfigTable.COLUMN_USUARIO_ID} INTEGER NOT NULL,
            ${ConfigTable.COLUMN_PASSWORD} TEXT DEFAULT "1234",
            
            ${ConfigTable.COLUMN_NUMERO_CIFRAS_SUMA} INTEGER DEFAULT 2,
            ${ConfigTable.COLUMN_TIEMPO_EXTRA_SUMA} INTEGER DEFAULT 20,
            ${ConfigTable.COLUMN_LLEVAS_SUMA} INTEGER DEFAULT 0,
            
            ${ConfigTable.COLUMN_NUMERO_CIFRAS_RESTA} INTEGER DEFAULT 2,
            ${ConfigTable.COLUMN_TIEMPO_EXTRA_RESTA} INTEGER DEFAULT 20,
            ${ConfigTable.COLUMN_LLEVAS_RESTA} INTEGER DEFAULT 0,
            
            ${ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICANDO} INTEGER DEFAULT 3,
            ${ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICADOR} INTEGER DEFAULT 2,
            ${ConfigTable.COLUMN_TIEMPO_EXTRA_MULTIPLICACION} INTEGER DEFAULT 10,
            
            ${ConfigTable.COLUMN_NUMERO_CIFRAS_DIVIDENDO} INTEGER DEFAULT 3,
            ${ConfigTable.COLUMN_NUMERO_CIFRAS_DIVISOR} INTEGER DEFAULT 1,
            ${ConfigTable.COLUMN_TIEMPO_EXTRA_DIVISION} INTEGER DEFAULT 20,
            ${ConfigTable.COLUMN_SOLO_EXACTAS_DIVISION} INTEGER DEFAULT 0,
            
            ${ConfigTable.COLUMN_TABLAS_MULTIPLICAR} TEXT DEFAULT "2,3,4,5,6,7,8,9",
            ${ConfigTable.COLUMN_TIEMPO_EXTRA_TABLAS_MULTIPLICAR} INTEGER DEFAULT 20,
            
            ${ConfigTable.COLUMN_TIEMPO_EXTRA_COMPRENSION} INTEGER DEFAULT 20,
            ${ConfigTable.COLUMN_EJERCICIOS_COMPRENSION} TEXT DEFAULT  "suma,resta,multiplicar,dividir",
               
            ${ConfigTable.COLUMN_NUMERO_PREGUNTAS_EXAMEN} INTEGER DEFAULT 10,
            ${ConfigTable.COLUMN_TIEMPO_EXTRA_EXAMEN} INTEGER DEFAULT 25,
            ${ConfigTable.COLUMN_LISTADO_MODULOS_EXAMEN} TEXT DEFAULT  "suma,resta,multiplicar,dividir,comprension,tablas"

        )
    """

    const val SQL_CREATE_PRUEBAS = """
        CREATE TABLE ${PruebasTable.TABLE_NAME} (
            ${PruebasTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PruebasTable.COLUMN_USUARIO} INTEGER NOT NULL,
            ${PruebasTable.COLUMN_FECHA} DATETIME DEFAULT CURRENT_TIMESTAMP,
            ${PruebasTable.COLUMN_ACIERTOS} INTEGER DEFAULT 0,
            ${PruebasTable.COLUMN_FALLOS} INTEGER DEFAULT 0,
            ${PruebasTable.COLUMN_MODULO} TEXT NOT NULL,
            ${PruebasTable.COLUMN_EXTRA_TOTAL} INTEGER DEFAULT 0,
            FOREIGN KEY (${PruebasTable.COLUMN_USUARIO}) REFERENCES ${UsuariosTable.TABLE_NAME}(${UsuariosTable.COLUMN_ID}) ON DELETE CASCADE
        )
    """


    const val SQL_CREATE_ERRORES = """
        CREATE TABLE ${ErroresTable.TABLE_NAME} (
            ${ErroresTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${ErroresTable.COLUMN_USUARIO} INTEGER NOT NULL,
            ${ErroresTable.COLUMN_OPERACION} TEXT NOT NULL,
            ${ErroresTable.COLUMN_NUM1} INTEGER NOT NULL,
            ${ErroresTable.COLUMN_NUM2} INTEGER NOT NULL,
            ${ErroresTable.COLUMN_FECHA} DATETIME DEFAULT CURRENT_TIMESTAMP,
            ${ErroresTable.COLUMN_RESPUESTA_CORRECTA} INTEGER,
            ${ErroresTable.COLUMN_RESPUESTA_USUARIO} INTEGER,
            FOREIGN KEY (${ErroresTable.COLUMN_USUARIO}) REFERENCES ${UsuariosTable.TABLE_NAME}(${UsuariosTable.COLUMN_ID}) ON DELETE CASCADE
        )
    """

    const val SQL_CREATE_ESTADISTICAS_TABLAS = """
    CREATE TABLE ${EstadisticasTablas.TABLE_NAME} (
        ${EstadisticasTablas.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${EstadisticasTablas.COLUMN_USUARIO} INTEGER NOT NULL,
        ${EstadisticasTablas.COLUMN_TABLA} INTEGER NOT NULL,
        ${EstadisticasTablas.COLUMN_REALIZADOS} INTEGER DEFAULT 0,
        ${EstadisticasTablas.COLUMN_FALLOS} INTEGER DEFAULT 0,
        ${EstadisticasTablas.COLUMN_ULTIMA_FECHA} DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (${EstadisticasTablas.COLUMN_USUARIO}) 
            REFERENCES ${UsuariosTable.TABLE_NAME}(${UsuariosTable.COLUMN_ID})
            ON DELETE CASCADE
    )
"""
    // SQL para eliminar las tablas
    const val SQL_DELETE_USUARIOS = "DROP TABLE IF EXISTS ${UsuariosTable.TABLE_NAME}"
    const val SQL_DELETE_PRUEBAS = "DROP TABLE IF EXISTS ${PruebasTable.TABLE_NAME}"
    const val SQL_DELETE_ERRORES = "DROP TABLE IF EXISTS ${ErroresTable.TABLE_NAME}"
    const val SQL_DELETE_ESTADISTICAS_TABLAS = "DROP TABLE IF EXISTS ${EstadisticasTablas.TABLE_NAME}"
    const val SQL_DELETE_CONFIGURACIONES = "DROP TABLE IF EXISTS ${ConfigTable.TABLE_NAME}"
    const val SQL_DELETE_DATOSMODULOS = "DROP TABLE IF EXISTS ${DatosModulosTable.TABLE_NAME}"


    // Índices para mejorar el rendimiento
    val SQL_CREATE_INDEXES = arrayOf(
        "CREATE INDEX IF NOT EXISTS idx_usuarios_nombre ON ${UsuariosTable.TABLE_NAME}(${UsuariosTable.COLUMN_NOMBRE})",
        "CREATE INDEX IF NOT EXISTS idx_pruebas_usuario ON ${PruebasTable.TABLE_NAME}(${PruebasTable.COLUMN_USUARIO})",
        "CREATE INDEX IF NOT EXISTS idx_pruebas_fecha ON ${PruebasTable.TABLE_NAME}(${PruebasTable.COLUMN_FECHA})",
        "CREATE INDEX IF NOT EXISTS idx_errores_usuario ON ${ErroresTable.TABLE_NAME}(${ErroresTable.COLUMN_USUARIO})",
        "CREATE INDEX IF NOT EXISTS idx_errores_fecha ON ${ErroresTable.TABLE_NAME}(${ErroresTable.COLUMN_FECHA})",
        "CREATE INDEX IF NOT EXISTS idx_errores_operacion ON ${ErroresTable.TABLE_NAME}(${ErroresTable.COLUMN_OPERACION})",
        "CREATE INDEX IF NOT EXISTS idx_estadisticas_usuario ON ${EstadisticasTablas.TABLE_NAME}(${EstadisticasTablas.COLUMN_USUARIO})",
        "CREATE INDEX IF NOT EXISTS idx_estadisticas_tabla ON ${EstadisticasTablas.TABLE_NAME}(${EstadisticasTablas.COLUMN_TABLA})"

    )
}