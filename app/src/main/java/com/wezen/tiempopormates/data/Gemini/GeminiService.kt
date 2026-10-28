package com.wezen.tiempopormates.data.Gemini


import android.util.Log
import com.wezen.tiempopormates.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume

private const val TAG = "GeminiServiceComprension"

data class HistoriaComprension(
    val historia: String,
    val respuesta: String
)


interface GeminiService {
    suspend fun obtenerHistoria(tipoOperacion: String): Result<HistoriaComprension>
}

class GeminiServiceImpl(

) : GeminiService {

    // La clave se inyecta en tiempo de compilación desde local.properties (ver app/build.gradle.kts)
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val promptTemplate = """
Entrada: ****OPERACION*** (Valor: suma, resta, multiplicacion o division)
Instrucciones de Salida:
1. Formato de Salida Obligatorio:
La respuesta debe estar en JSON, con esta estructura exacta:
{
  "problema": "Aquí va la historia y la pregunta final",
  "respuesta_operacion": "suma | resta | multiplicacion | division"
}
El valor de "respuesta_operacion" debe ser **exactamente igual** a la **OPERACION** proporcionada en la entrada.
2. Formato General:
El problema debe ser una pequeña historia de **máximo 100 palabras**.
Los personajes deben ser nombres comunes o personajes de animación actuales.
No resuelvas el problema, solo plantea la situación.
La operación matemática planteada en la historia debe ser **exacta** (sin sobras ni decimales, ni numeros negativos).
La historia debe incluir iconos (emojis) al inicio o al final de alguna frase, pero sin eliminar palabras.
Entre la historia y la pregunta final debe haber un **salto de línea**.
3. Pregunta Final:
La historia debe terminar con una pregunta cuya respuesta sea la operación necesaria, por ejemplo: "¿Cómo puede saber Ana el número total de figuritas que acumuló?".
No debe aparecer la frase "¿Qué operación necesitas?" ni nada parecido.
4. Verbos Permitidos:
La historia debe utilizar exclusivamente verbos de la lista correspondiente a la **OPERACION** de la entrada.
Verbos por Operación:
Suma
Añadir, Sumar, Juntar, Reunir, Agregar, Conseguir más, Obtener más, Ganar, Recoger, Acumular, Meter, Comprar, Llegan, Recibir, Incluir, Sumarle, Añadirse, Incorporar, Completar con más, Unirse, Acercarse más objetos, Recoger más, Aumentar, Acumular más unidades.
Resta
Quitar, Perder, Gastar, Restar, Dejar, Dar, Entregar, Regalar, Usar, Consumir, Vender, Salen, Romper, Reducir, Sacar, Apartar, Descontar, Retirar, Perder parte de, Bajar la cantidad, Hacer desaparecer, Gastarse, Dejar menos.
Multiplicacion
Agrupar, Repetir, Tener grupos de, Formar equipos de, Hacer paquetes de, Organizar filas de, Construir torres de, Colocar en filas/columnas, Poner la misma cantidad en cada grupo, Repartir en grupos iguales, Repetir varias veces, Tener lotes de, Hacer conjuntos de, Preparar cajas con, Crear grupos idénticos, Armar filas iguales, Organizar paquetes iguales, Poner la misma cantidad en cada fila, Contar bloques del mismo tamaño, Reunir grupos repetidos.
Division
Repartir, Dividir, Compartir, Distribuir, Separar en grupos iguales, Colocar por igual, Entregar a cada uno, Repartirse entre, Hacer partes iguales, Cortar en porciones, Separar por partes, Dividir por igual, Fraccionar, Partir en porciones, Repartir equitativamente, Dar la misma cantidad a cada uno, Distribuir por turnos, Hacer porciones iguales, Repartirse lo mismo, Dividir en secciones.
Ejemplo del tipo de salida esperada:
{
  "problema": "Lucas tenía 24 galletas en una bandeja. Después de que él y su hermana comieron algunas, solo quedaron 18 galletas. ¿Cómo podría saber Lucas cuántas galletas se comieron entre los dos?",
  "respuesta_operacion": "resta"
}
"""

    override suspend fun obtenerHistoria(tipoOperacion: String): Result<HistoriaComprension> {
        if (apiKey.isBlank()) {
            Log.e(TAG, "Falta GEMINI_API_KEY en local.properties")
            return Result.failure(IllegalStateException("Falta GEMINI_API_KEY en local.properties"))
        }

        return suspendCancellableCoroutine { continuation ->
            try {

                Log.d(TAG,"Operacion solicitada a Gemini ${tipoOperacion}")



                val promptCompleto = promptTemplate.replace("****OPERACION***", tipoOperacion)
                //Log.d(TAG,"Promt Final:  ${promptCompleto}")


                val json = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", promptCompleto)
                                })
                            })
                        })
                    })
                }

                val requestBody = json.toString()
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(baseUrl)
                    .addHeader("x-goog-api-key", apiKey)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "Error en la petición: ${e.message}")
                        continuation.resume(Result.failure(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val body = response.body?.string()

                            if (!response.isSuccessful || body == null) {
                                throw Exception("Respuesta no exitosa: ${response.code}")
                            }

                            val jsonResponse = JSONObject(body)
                            val text = jsonResponse
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text")

                            val cleanText = text
                                .replace("```json", "")
                                .replace("```", "")
                                .trim()

                            val json = JSONObject(cleanText)
                            val problema = json.getString("problema")
                            val respuestaOperacion = json.getString("respuesta_operacion")

                            val resultado = HistoriaComprension(problema, respuestaOperacion)
                            continuation.resume(Result.success(resultado))

                        } catch (e: Exception) {
                            Log.e(TAG, "Error al parsear respuesta: ${e.message}")
                            Log.e(TAG, "Respuesta completa: $json")

                            continuation.resume(Result.failure(e))
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "Error general: ${e.message}")
                continuation.resume(Result.failure(e))
            }
        }
    }
}
