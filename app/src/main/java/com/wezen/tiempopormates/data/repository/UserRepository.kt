package com.wezen.tiempopormates.data.repository

import android.util.Log
import com.wezen.tiempopormates.data.Database.DatabaseHelper
import com.wezen.tiempopormates.data.entity.ConfiguracionUsuario
import com.wezen.tiempopormates.data.entity.ConfigUsuarioExamen
import com.wezen.tiempopormates.data.entity.DatosUsuarioModulo
import com.wezen.tiempopormates.data.entity.EstadisticaTabla
import com.wezen.tiempopormates.data.entity.Examen
import com.wezen.tiempopormates.data.entity.User

private const val TAG = "UserRepository"


class UserRepository(private val dbHelper: DatabaseHelper) {

    fun getAllUsers(): List<User> {
        return try {
            dbHelper.getAllUsers()
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo usuarios", e)
            emptyList()
        }
    }

    fun createUser(nombre: String, avatar: String? = null): Long {
        return try {
            dbHelper.createUser(nombre, avatar)
        } catch (e: Exception) {
            Log.e(TAG, "Error creando usuario", e)
            -1
        }
    }

    fun deleteUser(userId: Long): Boolean{
        return try {
            dbHelper.borrarUser(userId)
            true
        }catch (e: Exception){
            Log.e(TAG, "Error borrando usuario", e)
            false
        }
    }

    fun obtenerEstadisticasUsuario(userId: Long): Boolean{
        return try {
            dbHelper.getEstadisticasTablaPorUsuario(userId)
            true
        }catch (e: Exception){
            Log.e(TAG, "Error al obterner las estadisticas del usuario", e)
            false
        }
    }

    fun obtenerEstadisticasTablasUsuario(userId: Long):Map<Int, EstadisticaTabla> {
        return try {
            dbHelper.getEstadisticasTablaPorUsuario(userId)

        }catch (e: Exception){
            Log.e(TAG, "Error al obterner las estadisticas de las tablas del usuario", e)
            emptyMap()
        }
    }

    fun obtenerUltimosExamenesUsuario(userId: Long, limite: Int = 5): List<Examen> {
        return try {
            dbHelper.getUltimosExamenes(userId,limite)

        }catch (e: Exception){
            Log.e(TAG, "Error al obterner los ultimos examenes de usuario", e)
            emptyList()

        }
    }


    fun CargarUsuario(userId: Long): User{
        Log.e(TAG, "Usuario buscado con ID: $userId")
        return try {

            val usuario = dbHelper.ObternerUserById(userId)
                ?: throw Exception("Usuario no encontrado")

            usuario
        } catch (e: Exception) {
            Log.e(TAG, "Usuario no encontrado", e)
            throw e   // O devuelve un User por defecto si tú quieres
        }
    }

    fun CargarConfigUsuario(userId: Long): ConfiguracionUsuario{
        Log.e(TAG, "Configuracion Usuario buscado con ID: $userId")
        return try {

            val configUsuario = dbHelper.ObternerConfigUserById(userId)
                ?: throw Exception("Configuracion Usuario no encontrado")

            configUsuario
        } catch (e: Exception) {
            Log.e(TAG, "Usuario no encontrado UserRepository", e)
            throw e   // O devuelve un User por defecto si tú quieres
        }
    }

    fun CargarConfigExamenes(userId: Long): ConfigUsuarioExamen {
        Log.e(TAG, "Usuario buscado con ID: $userId")
        return try {
            val usuario = dbHelper.loadconfigExamenUser(userId)
                ?: throw Exception("Usuario no encontrado")
            usuario
        } catch (e: Exception) {
            Log.e(TAG, "Usuario no encontrado", e)
            throw e   // O devuelve un User por defecto si tú quieres
        }
    }

    fun CargarDatosUsuario(userId: Long): DatosUsuarioModulo {
        Log.e(TAG, "DatosModulos Usuario buscado con ID: $userId")
        return try {
            val datosUsuario = dbHelper.cargarDatosModulosUser(userId)
                ?: throw Exception("Usuario no encontrado")
            datosUsuario
        } catch (e: Exception) {
            Log.e(TAG, "Usuario no encontrado", e)
            throw e   // O devuelve un User por defecto si tú quieres
        }
    }

}