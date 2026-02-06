package ec.edu.uce.appproductos.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.yalantis.ucrop.UCrop
import java.io.File
import com.yalantis.ucrop.UCropActivity

class CropImageContract : ActivityResultContract<Pair<Uri, Uri>, Uri?>() {

    override fun createIntent(context: Context, input: Pair<Uri, Uri>): Intent {
        val (sourceUri, destinationUri) = input

        val options = UCrop.Options().apply {
            // 1. ACTIVAR CONTROLES
            setFreeStyleCropEnabled(true) // Permite mover los bordes libremente
            setHideBottomControls(false)  // Muestra la barra inferior con opciones

            // 2. CONFIGURAR COLORES (Para asegurar que se vean los iconos)
            setToolbarColor(android.graphics.Color.parseColor("#FF9800")) // Naranja (Tu marca)
            setStatusBarColor(android.graphics.Color.parseColor("#F57C00")) // Naranja oscuro
            setToolbarWidgetColor(android.graphics.Color.WHITE) // Texto e iconos blancos
            setActiveControlsWidgetColor(android.graphics.Color.parseColor("#FF9800")) // Color del control activo

            // Fondo oscuro para resaltar la imagen
            setRootViewBackgroundColor(android.graphics.Color.parseColor("#000000"))

            // 3. GESTOS PERMITIDOS (Zoom, Rotar, Escalar)
            setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)

            // 4. CALIDAD Y FORMATO
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(90)
            setToolbarTitle("Editar Foto")
        }

        return UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(1f, 1f) // Aspecto inicial (cuadrado), pero modificable gracias a FreeStyle
            .getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return UCrop.getOutput(intent)
        }
        // Manejo de errores: Si uCrop devuelve un error, lo imprimimos en consola
        if (resultCode == UCrop.RESULT_ERROR && intent != null) {
            val error = UCrop.getError(intent)
            error?.printStackTrace()
        }
        return null
    }
}

// Función auxiliar (igual que antes)
fun crearArchivoDestinoCrop(context: Context): Uri {
    val nombre = "recorte_${System.currentTimeMillis()}.jpg"
    val archivo = File(context.cacheDir, nombre)
    return Uri.fromFile(archivo)
}