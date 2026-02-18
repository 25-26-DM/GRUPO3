package ec.edu.uce.appproductos.controller

import android.util.Log
import ec.edu.uce.appproductos.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// Interfaz que define el endpoint del servicio web
interface MailInsertRecApiService {
    @FormUrlEncoded
    @POST("mailinsertrec") // Asumimos que este es el nombre del endpoint
    suspend fun enviarCorreoProducto(
        @Field("destinatario") destinatario: String,
        @Field("codigo") codigo: String,
        @Field("descripcion") descripcion: String,
        @Field("fechaFabricacion") fechaFabricacion: String,
        @Field("costo") costo: Double,
        @Field("isDisponible") isDisponible: Boolean
    ): Response<Unit> // Usamos Response<Unit> para ver el código HTTP, sin esperar un cuerpo de respuesta específico
}

// Objeto gestor para el servicio de correo
object EmailService {

    // Reemplaza con la URL BASE de tu servicio web. Debe terminar con "/"
    private const val BASE_URL = "https://tuservidor.com/api/"

    private val api: MailInsertRecApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MailInsertRecApiService::class.java)
    }

    suspend fun notificarNuevoProducto(
        destinatario: String,
        producto: Producto
    ): Boolean = withContext(Dispatchers.IO) {
        // Validar que el destinatario no esté vacío
        if (destinatario.isBlank()) {
            Log.e("EMAIL_SERVICE", "Error: El destinatario del correo está vacío.")
            return@withContext false
        }

        return@withContext try {
            val response = api.enviarCorreoProducto(
                destinatario = destinatario,
                codigo = producto.codigo,
                descripcion = producto.descripcion,
                fechaFabricacion = producto.fechaFabricacion,
                costo = producto.costo,
                isDisponible = producto.isDisponible
            )

            if (response.isSuccessful) {
                Log.i("EMAIL_SERVICE", "Correo enviado exitosamente para producto: ${producto.codigo}")
                true
            } else {
                Log.e("EMAIL_SERVICE", "Error al enviar correo. Código: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("EMAIL_SERVICE", "Excepción en la llamada de red: ${e.message}")
            false
        }
    }
}