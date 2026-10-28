package com.wezen.tiempopormates.data.Database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseContract.DATABASE_NAME
import com.wezen.tiempopormates.data.Database.DatabaseContract.DATABASE_VERSION
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.ConfigUsuarioExamen
import com.wezen.tiempopormates.data.entity.DatosUsuarioModulo
import com.wezen.tiempopormates.data.entity.EstadisticaTabla
import com.wezen.tiempopormates.data.entity.Examen
import com.wezen.tiempopormates.data.entity.User



class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null,
    DATABASE_VERSION
    ){
    companion object {
        private const val TAG = "dbHelper"

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    override fun onCreate(db: SQLiteDatabase) {
        try {
            // Crear las tablas llamado a las consultas del DatabaseContract
            db.execSQL(DatabaseContract.SQL_CREATE_USUARIOS)
            db.execSQL(DatabaseContract.SQL_CREATE_PRUEBAS)
            db.execSQL(DatabaseContract.SQL_CREATE_ERRORES)
            db.execSQL(DatabaseContract.SQL_CREATE_ESTADISTICAS_TABLAS)
            db.execSQL(DatabaseContract.SQL_CREATE_CONFIGURACIONES)
            db.execSQL(DatabaseContract.SQL_CREATE_DATOSMODULOS)

            // Crear índices
            DatabaseContract.SQL_CREATE_INDEXES.forEach { index ->
                db.execSQL(index)
            }

            Log.d(TAG, "Base de datos creada correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear la base de datos", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL(DatabaseContract.SQL_DELETE_ERRORES)
            db.execSQL(DatabaseContract.SQL_DELETE_PRUEBAS)
            db.execSQL(DatabaseContract.SQL_DELETE_USUARIOS)
            db.execSQL(DatabaseContract.SQL_DELETE_ESTADISTICAS_TABLAS)
            db.execSQL(DatabaseContract.SQL_DELETE_CONFIGURACIONES)
            db.execSQL(DatabaseContract.SQL_DELETE_DATOSMODULOS)

            onCreate(db)
            Log.d(TAG, "Base de datos actualizada de versión $oldVersion a $newVersion")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar la base de datos", e)
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // =========================================== FUNCIONES PARA USUARIOS =============================================================
    /**
     * Funcion que crea un usuario en la base de datos y devuelve el ID del usuario creado.
     * @param nombre Nombre del usuario.
     * @param avatar Avatar del usuario.
     * @return ID del usuario creado.
     */
    fun createUser(nombre: String, avatar: String? = null): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.UsuariosTable.COLUMN_NOMBRE, nombre)
            put(DatabaseContract.UsuariosTable.COLUMN_AVATAR, avatar)
        }
        return try {
            val id = db.insert(DatabaseContract.UsuariosTable.TABLE_NAME, null, values)
            Log.d(TAG, "Usuario creado con ID: $id")
            initializeUserStatisticsTables(id)//Iniciamos los datos de las tablas a 0
            initializeDatosModulos(id)// Iniciamos las estadisticas de los modulos
            initializeConfigUser(id)//Inicializamos su tablla de configuracion
            id
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear usuario", e)
            -1
        }

    }

    /**
     * Funcion para crear todos los registros de las tablas para un usuario en concreto poniendo estos de inicio a 0
     *  y la fecha de ese momento
     * @param usuarioId Id del usuario a crear los registros.
     */
    fun initializeUserStatisticsTables(usuarioId: Long) {
        val db = writableDatabase
        db.beginTransaction()

        try {
            for (tabla in 1..9) {
                val values = ContentValues().apply {
                    put(DatabaseContract.EstadisticasTablas.COLUMN_USUARIO, usuarioId)
                    put(DatabaseContract.EstadisticasTablas.COLUMN_TABLA, tabla)
                    put(DatabaseContract.EstadisticasTablas.COLUMN_REALIZADOS, 0)
                    put(DatabaseContract.EstadisticasTablas.COLUMN_FALLOS, 0)
                    put(DatabaseContract.EstadisticasTablas.COLUMN_ULTIMA_FECHA, System.currentTimeMillis())
                }

                db.insert(DatabaseContract.EstadisticasTablas.TABLE_NAME, null, values)
            }

            db.setTransactionSuccessful()
            Log.d(TAG, "Estadísticas inicializadas para el usuario $usuarioId")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar estadísticas de usuario", e)
        } finally {
            db.endTransaction()
        }
    }

    fun initializeDatosModulos(usuarioId: Long) {
        val db = writableDatabase
        db.beginTransaction()
        val modulos = listOf("suma", "resta", "multiplicar", "dividir", "tablas", "comprension")

        try {

            modulos.forEach { modulo ->
                val values = ContentValues().apply {
                    put(DatabaseContract.DatosModulosTable.COLUMN_MODULO, modulo)
                    put(DatabaseContract.DatosModulosTable.COLUMN_USUARIO_ID, usuarioId)
                    put(DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS, 0)
                    put(DatabaseContract.DatosModulosTable.COLUMN_FALLOS, 0)
                }

                db.insert(DatabaseContract.DatosModulosTable.TABLE_NAME, null, values)
            }


            db.setTransactionSuccessful()
            Log.d(TAG, "DatosModulos inicializados para el usuario $usuarioId")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar DatosModulos de usuario", e)
        } finally {
            db.endTransaction()
        }
    }

    fun initializeConfigUser(usuarioId: Long) {

        val db = writableDatabase
        db.beginTransaction()
        val values = ContentValues().apply {
            put(DatabaseContract.ConfigTable.COLUMN_USUARIO_ID, usuarioId)
            put(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_SUMA, 2)
            put(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_SUMA, 10)
            put(DatabaseContract.ConfigTable.COLUMN_LLEVAS_SUMA, 1)

            put(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_RESTA, 2)
            put(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_RESTA, 10)
            put(DatabaseContract.ConfigTable.COLUMN_LLEVAS_RESTA, 1)

            put(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICANDO, 2)
            put(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICADOR, 2)
            put(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_MULTIPLICACION, 10)

            put(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_DIVIDENDO, 2)
            put(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_DIVISOR, 2)
            put(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_DIVISION, 10)
            put(DatabaseContract.ConfigTable.COLUMN_SOLO_EXACTAS_DIVISION, 1)

            put(DatabaseContract.ConfigTable.COLUMN_NUMERO_PREGUNTAS_EXAMEN, 10)
            put(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_EXAMEN, 10)
            put(DatabaseContract.ConfigTable.COLUMN_LISTADO_MODULOS_EXAMEN, "suma,resta,multiplicar,dividir,tablas,comprension")

            put(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_COMPRENSION, 10)
            put(DatabaseContract.ConfigTable.COLUMN_EJERCICIOS_COMPRENSION, "suma,resta,multiplicar,dividir")

            put(DatabaseContract.ConfigTable.COLUMN_TABLAS_MULTIPLICAR, "2,3,4,5,6,7,8,9")
            put(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_TABLAS_MULTIPLICAR, 10)

            put(DatabaseContract.ConfigTable.COLUMN_PASSWORD, "1234")
        }

        try {
            db.insert(DatabaseContract.ConfigTable.TABLE_NAME, null, values)
            db.setTransactionSuccessful()
            Log.d(TAG, "Configuracion Usuario creado con ID: $usuarioId")


        } catch (e: Exception) {
            Log.e(TAG, "Error al crear Configuracion usuario", e)
        } finally {
            db.endTransaction()
        }

    }

    fun getAllUsers(): List<User> {
        val usuarios = mutableListOf<User>()
        val db = readableDatabase

        val cursor = db.query(
            DatabaseContract.UsuariosTable.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            DatabaseContract.UsuariosTable.COLUMN_NOMBRE
        )

        cursor.use {
            while (it.moveToNext()) {
                usuarios.add(cursorToUsuario(it))
            }
        }

        Log.d(TAG, "Lista Usuarios: $usuarios")
        return usuarios
    }

    fun ObternerUserById(id: Long): User? {
        Log.d(TAG, "Usuario a buscar: $id")
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.UsuariosTable.TABLE_NAME,
            null,
            "${DatabaseContract.UsuariosTable.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                cursorToUsuario(it)
            } else {
                null
            }
        }
    }

    fun ObternerConfigUserById(id: Long): ConfiguracionUsuario? {
        val db = readableDatabase
        val cursor = db.query(
            DatabaseContract.ConfigTable.TABLE_NAME,
            null,
            "${DatabaseContract.ConfigTable.COLUMN_USUARIO_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                cursorToConfig(it)
            } else {
                null
            }
        }
    }

    fun borrarUser(userId: Long?): Boolean {
        return try {
            val db = this.writableDatabase
            db.beginTransaction()

            try {
                // Borramos los datos de la tabla de errores
                db.delete(
                    DatabaseContract.ErroresTable.TABLE_NAME,
                    "${DatabaseContract.ErroresTable.COLUMN_USUARIO} = ?",
                    arrayOf(userId.toString())
                )

                // Borramos datos de la tabla de pruebas
                db.delete(
                    DatabaseContract.PruebasTable.TABLE_NAME,
                    "${DatabaseContract.PruebasTable.COLUMN_USUARIO} = ?",
                    arrayOf(userId.toString())
                )

                // Borramos datos de la tabla de estadísticas de tablas
                db.delete(
                    DatabaseContract.EstadisticasTablas.TABLE_NAME,
                    "${DatabaseContract.EstadisticasTablas.COLUMN_USUARIO} = ?",
                    arrayOf(userId.toString())
                )

                // Borramos los datos de la tabla de usuarios
                val rowsDeleted = db.delete(
                    DatabaseContract.UsuariosTable.TABLE_NAME,
                    "${DatabaseContract.UsuariosTable.COLUMN_ID} = ?",
                    arrayOf(userId.toString())
                )


                if (rowsDeleted > 0) {
                    db.setTransactionSuccessful()
                    Log.d(TAG, "Usuario  borrado correctamente")
                    true
                } else {
                    false

                }
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    //----------FUNCIONES DatosModulos---------------------------------------------------------------------------------

    /**
     * Obtiene los realizados de un módulo específico de un usuario
     * @param usuarioId ID del usuario
     * @param nombreModulo Nombre del módulo
     * @return Número de ejercicios realizados en ese módulo
     */
    fun ejerciciosRealizadosPorUsuario(usuarioId: Long): Int {
        val db = readableDatabase

        val cursor = db.query(
            DatabaseContract.DatosModulosTable.TABLE_NAME,
            arrayOf("SUM(${DatabaseContract.DatosModulosTable.COLUMN_REALIZADOS}) as total"),
            "${DatabaseContract.DatosModulosTable.COLUMN_USUARIO_ID} = ?",
            arrayOf(usuarioId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                val totalColumnIndex = it.getColumnIndex("total")
                if (totalColumnIndex != -1) {
                    it.getInt(totalColumnIndex)
                } else {
                    0
                }
            } else {
                0
            }
        }
    }


    // -----------------------------------------Funciones ERRORES
    /***
     * Devuelve un Array con los numeros del 0 al 9 y sus repeticiones de la tabla de errores del usuario
     * @param usuarioId Id del usuario a contar los errores
     * @param operacion Operacion a contar los errores
     * @return Array con los numeros del 0 al 9 y sus repeticiones
     */
    fun contarErroresUsuario(usuarioId: Long?, operacion: String): Array<Int> {


        val arrayConteoErrores = Array(10) { 0 }

        val columns = arrayOf(
            DatabaseContract.ErroresTable.COLUMN_NUM1,
            DatabaseContract.ErroresTable.COLUMN_NUM2
        )

        val db = readableDatabase
        var totalContNumero: Int = 0
        val cursor = db.query(
            DatabaseContract.ErroresTable.TABLE_NAME,
            columns,
            "${DatabaseContract.ErroresTable.COLUMN_USUARIO} = ? AND ${DatabaseContract.ErroresTable.COLUMN_OPERACION} = ?",
            arrayOf(usuarioId.toString(), operacion),
            null,
            null,
            null
        )

        cursor.use {
            val num1Index = it.getColumnIndexOrThrow(DatabaseContract.ErroresTable.COLUMN_NUM1)
            val num2Index = it.getColumnIndexOrThrow(DatabaseContract.ErroresTable.COLUMN_NUM2)

            while (it.moveToNext()) {

                val num1 = it.getInt(num1Index)
                val num2 = it.getInt(num2Index)

                if (num1 in 0..9) {
                    arrayConteoErrores[num1]++
                    totalContNumero += 1
                }

                // Contabilizar Num2 (debe estar en el rango 0-9)
                if (num2 in 0..9) {
                    arrayConteoErrores[num2]++
                    totalContNumero += 1
                }
            }

        }
        Log.d(TAG, "Conteo de errores para usuario $usuarioId: en la operacion $operacion =>${arrayConteoErrores.joinToString()}")
        return arrayConteoErrores
    }

    // =========================================== FUNCIONES PARA EXAMENES =============================================================

    /**
     * Funcion dinamica para actualizar los datos del usuario de forma dinamica
     * @param usuarioId Id del usuario a actualizar
     * @param updates Mapa de datos a actualizar. Se ha decidido crear un Map para poder llamar a esta funcion en cada caso con los parametros concretos a actualizar.
     * @return True si se ha actualizado correctamente, false en caso contrario.
     */
    fun insertarExamen(usuarioId: Long?, updates: Map<String, String>): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.PruebasTable.COLUMN_USUARIO, usuarioId)
            put(DatabaseContract.PruebasTable.COLUMN_FECHA, System.currentTimeMillis())
            put(DatabaseContract.PruebasTable.COLUMN_ACIERTOS, updates["aciertos"] )
            put(DatabaseContract.PruebasTable.COLUMN_FALLOS, updates["fallos"])
            put(DatabaseContract.PruebasTable.COLUMN_MODULO, updates["modulos"])
            put(DatabaseContract.PruebasTable.COLUMN_EXTRA_TOTAL, updates["tiempo"])
        }
        return try {
            val id = db.insert(DatabaseContract.PruebasTable.TABLE_NAME, null, values)
            Log.d(TAG, "Examen añadido con ID: $id")
            id
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar el examen", e)
            -1
        }

    }

    fun getUltimosExamenes(usuarioId: Long, limite: Int = 5): List<Examen> {
        val db = writableDatabase
        val cursor = db.query(
            DatabaseContract.PruebasTable.TABLE_NAME,
            null,
            "${DatabaseContract.PruebasTable.COLUMN_USUARIO} = ?",
            arrayOf(usuarioId.toString()),
            null,
            null,
            "${DatabaseContract.PruebasTable.COLUMN_FECHA} DESC",
            limite.toString()
        )

        val examenes = mutableListOf<Examen>()
        while (cursor.moveToNext()) {
            examenes.add(
                Examen(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PruebasTable.COLUMN_ID)),
                    usuarioId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PruebasTable.COLUMN_USUARIO)),
                    fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PruebasTable.COLUMN_FECHA)),
                    aciertos = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PruebasTable.COLUMN_ACIERTOS)),
                    fallos = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PruebasTable.COLUMN_FALLOS)),
                    modulos = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PruebasTable.COLUMN_MODULO)),
                    tiempo = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PruebasTable.COLUMN_EXTRA_TOTAL))
                )

            )

            Log.d(TAG, "Examen ${examenes.last()}")
        }
        cursor.close()
        return examenes
    }


    //==============================================FUNCIONES ESTADISTICAS====================================================================================
    /**
     * Funcion dinamica para actualizar los datos del usuario de forma dinamica
     * @param usuarioId Id del usuario a actualizar
     * @param updates Mapa de datos a actualizar. Se ha decidido crear un Map para poder llamar a esta funcion en cada caso con los parametros concretos a actualizar.
     * @return True si se ha actualizado correctamente, false en caso contrario.
     */
    fun updateUser(usuarioId: Long?, updates: Map<String, Int>): Boolean {
        if (updates.isEmpty()) return false

        val db = writableDatabase

        return try {
            // Construir la cláusula SET dinámicamente
            val setClauses = updates.map { (column, increment) ->
                "$column = $column + $increment"
            }.joinToString(", ")

            val sql = """
            UPDATE ${DatabaseContract.UsuariosTable.TABLE_NAME} 
            SET $setClauses 
            WHERE ${DatabaseContract.UsuariosTable.COLUMN_ID} = ?
        """.trimIndent()

            db.execSQL(sql, arrayOf(usuarioId.toString()))
            Log.d(TAG, "Estadísticas actualizadas para usuario $usuarioId: $updates")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estadísticas del usuario", e)
            false
        }
    }

    fun cargarDatosModulosUser(usuarioId: Long): DatosUsuarioModulo? {
        val db = readableDatabase

        // Valores por defecto
        var sumaR = 0; var sumaF = 0
        var restaR = 0; var restaF = 0
        var multiR = 0; var multiF = 0
        var divR = 0; var divF = 0
        var compR = 0; var compF = 0

        val cursor = db.rawQuery(
            """
            SELECT Modulo, realizados, fallos
            FROM DatosModulosTable
            WHERE UsuarioId = ?
            """.trimIndent(),
            arrayOf(usuarioId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val modulo = it.getString(0)
                val realizados = it.getInt(1)
                val fallos = it.getInt(2)

                when (modulo) {
                    "suma" -> { sumaR = realizados; sumaF = fallos }
                    "resta" -> { restaR = realizados; restaF = fallos }
                    "multiplicar" -> { multiR = realizados; multiF = fallos }
                    "dividir" -> { divR = realizados; divF = fallos }
                    "comprension" -> { compR = realizados; compF = fallos }
                }
            }
        }

        return DatosUsuarioModulo(
            usuarioId = usuarioId,

            sumaRealizados = sumaR,
            sumaFallos = sumaF,

            restaRealizados = restaR,
            restaFallos = restaF,

            multiplicacionRealizados = multiR,
            multiplicacionFallos = multiF,

            divisionRealizados = divR,
            divisionFallos = divF,

            comprensionRealizados = compR,
            comprensionFallos = compF,

        )

    }

    fun updateUserEstadisticasModulo(usuarioId: Long?,modulo:String?, updates: Map<String, Int>): Boolean {
        if (updates.isEmpty()) return false
        val db = writableDatabase

        return try {
            // Construir la cláusula SET dinámicamente
            val setClauses = updates.map { (column, increment) ->
                "$column = $column + $increment"
            }.joinToString(", ")

            Log.e(TAG, "UPDATE DATOS ($modulo): $setClauses")

            val sql = """
            UPDATE ${DatabaseContract.DatosModulosTable.TABLE_NAME} 
            SET $setClauses 
            WHERE ${DatabaseContract.DatosModulosTable.COLUMN_USUARIO_ID} = ?
            AND ${DatabaseContract.DatosModulosTable.COLUMN_MODULO} = ?
        """.trimIndent()

            db.execSQL(sql, arrayOf(usuarioId.toString(),modulo))

            Log.d(TAG, "Estadísticas actualizadas dentro de Modulo para usuario $usuarioId: $modulo->$updates")
            if (usuarioId != null) cargarDatosModulosUser(usuarioId)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estadísticas modulo del usuario", e)
            false
        }
    }

    //=============================================COFIGURACIONES=========

    fun guardaConfiguracion(usuarioId: Long?, updates: Map<String, String>): Boolean {
        if (updates.isEmpty()) return false

        if (usuarioId == null) {
            Log.e(TAG, "❌ ERROR: usuarioId es null")
            return false
        }

        val db = writableDatabase


        return try {
            db.beginTransaction()

            // Construir la cláusula SET dinámicamente
            val setClauses = updates.map { (column, nuevoValor) ->
                // Escapar comillas simples en el valor
                val valorEscapado = nuevoValor.replace("'", "''")
                "$column = '$valorEscapado'"
            }.joinToString(", ")

            val sql = """
            UPDATE ${DatabaseContract.ConfigTable.TABLE_NAME} 
            SET $setClauses 
            WHERE ${DatabaseContract.ConfigTable.COLUMN_USUARIO_ID} = ?
        """.trimIndent()

            db.execSQL(sql, arrayOf(usuarioId.toString()))
            Log.d(TAG, "Configuracion actualizadas para usuario $usuarioId: $updates")
            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estadísticas del usuario", e)
            false
        }finally {
            db.endTransaction()

        }
    }

    fun loadconfigExamenUser(usuarioId: Long?): ConfigUsuarioExamen? {
       /* val db = writableDatabase
        val cursor = db.query(
            DatabaseContract.UsuariosTable.TABLE_NAME,
            arrayOf(
                DatabaseContract.UsuariosTable.COLUMN_ID,
                DatabaseContract.UsuariosTable.COLUMN_NOMBRE,
                /*DatabaseContract.UsuariosTable.COLUMN_NUMERO_PREGUNTAS_EXAMEN,
                DatabaseContract.UsuariosTable.COLUMN_TIEMPO_EXTRA_EXAMEN,
                DatabaseContract.UsuariosTable.COLUMN_LISTADO_MODULOS_EXAMEN*/
            ),
                 //"COLUMN_NUMERO_PREGUNTAS_EXAMEN,COLUMN_TIEMPO_EXTRA_EXAMEN,COLUMN_LISTADO_MODULOS_EXAMEN"
            "${DatabaseContract.UsuariosTable.COLUMN_ID} = ?",
            arrayOf(usuarioId.toString()),
            null,
            null,
            "${DatabaseContract.UsuariosTable.COLUMN_ID} DESC",

        )
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_NOMBRE))
            val numeroPreguntasExamen = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_NUMERO_PREGUNTAS_EXAMEN))
            val tiempoExtraExamen = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_TIEMPO_EXTRA_EXAMEN))
            val listadoModulosExamen = try {
                val valor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_LISTADO_MODULOS_EXAMEN))

                // Si está null o vacío, usar todos los módulos
                valor?.takeIf { it.isNotBlank() }
                    ?: "suma,resta,multiplicacion,division,tablas,comprension"
            } catch (e: Exception) {
                Log.w(TAG, "No se encontró COLUMN_LISTADO_MODULOS_EXAMEN, usando todos los módulos")
                "suma,resta,multiplicacion,division,tablas,comprension"
            }
           // Log.d(TAG, "Configuración encontrada para $nombre: $numeroPreguntasExamen preguntas de los modulos $listadoModulosExamen")


            return ConfigUsuarioExamen(
                id,
                nombre,
                numeroPreguntasExamen,
                tiempoExtraExamen,
                listadoModulosExamen)

        }
*/

        return null

    }


    /**
     * Funcion dinamica para actualizar los datos de las tablas de multiplicar del usuario, el funcionamiento es exactamente igual que el anterior pero a la tabla de estadisiticasTablas
     * Se ha decido realizarla del mismo modo para no tener qwue implementar una funcion para cada actualizacion diferente
     * @param usuarioId Id del usuario a actualizar
     * @param updates Mapa de datos a actualizar. Se ha decidido crear un Map para poder llamar a esta funcion en cada caso con los parametros concretos a actualizar.
     * @return True si se ha actualizado correctamente, false en caso contrario.
     */
    fun updateUserStatisticsTablas(usuarioId: Long?,tabla:Int, updates: Map<String, Int>): Boolean  {
        if (updates.isEmpty()) return false

        val db = writableDatabase

        return try {
            // Construir la cláusula SET dinámicamente
            val setClauses = updates.map { (column, increment) ->
                "$column = $column + $increment"
            }.joinToString(", ")

            val sql = """
            UPDATE ${DatabaseContract.EstadisticasTablas.TABLE_NAME}
            SET $setClauses,
                ${DatabaseContract.EstadisticasTablas.COLUMN_ULTIMA_FECHA} = CURRENT_TIMESTAMP
            WHERE ${DatabaseContract.EstadisticasTablas.COLUMN_USUARIO} = ?
            AND ${DatabaseContract.EstadisticasTablas.COLUMN_TABLA} = ?
        """.trimIndent()

            db.execSQL(sql, arrayOf(usuarioId.toString(),tabla.toString()))
            Log.d(TAG, "Estadísticas Tablas actualizadas para usuario $usuarioId, Tabla $tabla:      Valor: $updates")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estadísticas Tablas del usuario", e)
            false
        }
    }

    fun getEstadisticasTablaPorUsuario(usuarioId: Long?): Map<Int, EstadisticaTabla> {
        val db = readableDatabase
        val estadisticas = mutableMapOf<Int, EstadisticaTabla>()
        Log.e(TAG, "Cargando las estadisticas de las tablas del usuario $usuarioId", )
        val cursor = db.rawQuery(
            """
        SELECT ${DatabaseContract.EstadisticasTablas.COLUMN_TABLA},
               ${DatabaseContract.EstadisticasTablas.COLUMN_REALIZADOS},
               ${DatabaseContract.EstadisticasTablas.COLUMN_FALLOS},
               ${DatabaseContract.EstadisticasTablas.COLUMN_ULTIMA_FECHA}
        FROM ${DatabaseContract.EstadisticasTablas.TABLE_NAME}
        WHERE ${DatabaseContract.EstadisticasTablas.COLUMN_USUARIO} = ?
        """.trimIndent(),
            arrayOf(usuarioId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val tabla = it.getInt(0)
                val realizados = it.getInt(1)
                val fallos = it.getInt(2)
                val fecha = it.getString(3)
                //Calculo el porcentaje de aciertos para la tabla de la que obtengo los datos y lo añado a la respuesta de la funcion

                val porcentajeAciertos: Double = if (realizados > 0) {
                    ((realizados - fallos).toDouble()* 100 / realizados.toDouble())
                } else {
                    0.0
                }

                estadisticas[tabla] = EstadisticaTabla(tabla, realizados, fallos,porcentajeAciertos,)
            }
        }

        Log.e(TAG, "Estadisiticas de las tablas del usuario: $estadisticas", )

        db.close()
        return estadisticas
    }

    fun añadirErroresUsuario(usuarioId: Long?, num1: String, num2: String, operacion: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.ErroresTable.COLUMN_USUARIO, usuarioId)
            put(DatabaseContract.ErroresTable.COLUMN_FECHA, System.currentTimeMillis())
            put(DatabaseContract.ErroresTable.COLUMN_NUM1, num1)
            put(DatabaseContract.ErroresTable.COLUMN_NUM2, num2)
            put(DatabaseContract.ErroresTable.COLUMN_OPERACION, operacion)
        }
        return try {
            val id = db.insert(DatabaseContract.ErroresTable.TABLE_NAME, null, values)
            Log.d(TAG, "Errores Añadidos a: $id, con los siguientes datos: $operacion,( $num1, $num2)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro al añadir el errro cometido por el usuario", e)
            false
        }

    }

    private fun cursorToUsuario(cursor: Cursor): User {

        return User(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_ID)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_NOMBRE)),
            avatar = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_AVATAR)),
            /*password = try {
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_PASSWORD)) ?: "1234"
            } catch (e: Exception) {
                "1234"
            },*/
            tiempo = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.UsuariosTable.COLUMN_TIEMPO)),

            fechaCreacion = null,
        )

    }
    private fun cursorToConfig(cursor: Cursor): ConfiguracionUsuario {
        return ConfiguracionUsuario(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_ID)),
            usuarioId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_ID)),

            //Suma
            numeroCifrasSuma = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_SUMA)),
            tiempoExtraSuma = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_SUMA)),
            llevadasSuma = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_LLEVAS_SUMA))==1,
            //Resta
            numeroCifrasResta = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_RESTA)),
            tiempoExtraResta = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_RESTA)),
            llevadasResta = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_LLEVAS_RESTA)) == 1,

            // Configuración MULTIPLICACIÓN
            numeroCifrasMultiplicando = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICANDO)),
            numeroCifrasMultiplicador = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_MULTIPLICADOR)),
            tiempoExtraMultiplicacion = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_MULTIPLICACION)),

            // Configuración DIVISIÓN
            numeroCifrasDividendo = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_DIVIDENDO)),
            numeroCifrasDivisor = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_NUMERO_CIFRAS_DIVISOR)),
            tiempoExtraDivision = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_DIVISION)),
            soloExactasDivision = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_SOLO_EXACTAS_DIVISION)) == 1,

            // Configuración TABLAS
            tablasMultiplicar = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TABLAS_MULTIPLICAR)) ?: "1,2,3,4,5,6,7,8,9,10",
            tiempoExtratablasMultiplicar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_TABLAS_MULTIPLICAR)),

            // Configuración COMPRENSIÓN
            tiempoExtraComprension = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_COMPRENSION)),
            ejerciciosComprension = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_EJERCICIOS_COMPRENSION)),

            // Configuración EXAMEN
            numeroPreguntasExamen = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_NUMERO_PREGUNTAS_EXAMEN)),
            tiempoExtraExamen = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_TIEMPO_EXTRA_EXAMEN)),
            listadoModulosExamen = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ConfigTable.COLUMN_LISTADO_MODULOS_EXAMEN)) ?: "suma,resta,multiplicar,dividir,comprension,tablas",

        )
    }
}