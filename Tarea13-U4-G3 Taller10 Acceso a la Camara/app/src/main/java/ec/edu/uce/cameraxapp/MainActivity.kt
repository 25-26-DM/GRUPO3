package ec.edu.uce.cameraxapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ec.edu.uce.cameraxapp.navigation.AppNavigation
import ec.edu.uce.cameraxapp.ui.theme.AccesoCamaraTheme

class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicitar permisos de cámara y audio
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        setContent {
            AccesoCamaraTheme {
                AppNavigation()
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
