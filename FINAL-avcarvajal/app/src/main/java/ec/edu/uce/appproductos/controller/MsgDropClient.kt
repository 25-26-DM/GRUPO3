package ec.edu.uce.appproductos.controller

import android.os.Handler
import android.os.Looper
import java.net.HttpURLConnection
import java.net.URL

object MsgDropClient {
    // Reemplaza con la URL que te dio tu profesor o la del servicio
    private const val SERVICE_URL = "https://api.msgdrop.io/v1/error-mensaje"

    fun obtenerMensaje(onResult: (String) -> Unit) {
        Thread {
            val mensaje = try {
                val conn = URL(SERVICE_URL).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                "imposible editar/eliminar Producto" // Fallback por defecto
            }
            // Regresamos al hilo principal para que el UI pueda mostrarlo
            Handler(Looper.getMainLooper()).post { onResult(mensaje) }
        }.start()
    }
}