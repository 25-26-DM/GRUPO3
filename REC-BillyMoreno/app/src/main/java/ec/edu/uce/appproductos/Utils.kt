package ec.edu.uce.appproductos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// --- UTILIDADES GENERALES ---

object Utils {

    // 1. Genera un CÓDIGO alfanumérico corto (Ej: "PROD-A1B2")
    fun generarCodigoProducto(): String {
        // Toma los primeros 6 caracteres de un UUID y los pone en mayúsculas
        val uuidParcial = UUID.randomUUID().toString().substring(0, 6).uppercase()
        return "PROD-$uuidParcial"
    }

    // 2. Convierte Milisegundos (del DatePicker) a Texto legible (dd/MM/yyyy)
    fun formatearFechaMilis(milis: Long?): String {
        if (milis == null) return ""
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Ajuste de zona horaria para que la fecha sea exacta
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return formatter.format(Date(milis))
    }
}