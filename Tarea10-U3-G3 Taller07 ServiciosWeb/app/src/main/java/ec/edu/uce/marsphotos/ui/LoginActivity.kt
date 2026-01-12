package ec.edu.uce.marsphotos.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ec.edu.uce.marsphotos.MainActivity
import ec.edu.uce.marsphotos.ui.screens.LoginScreen
import ec.edu.uce.marsphotos.ui.theme.MarsPhotosTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarsPhotosTheme {
                // Llamamos a la pantalla visual del Login
                LoginScreen(
                    onLoginSuccess = { usuario, hora ->
                        // CUANDO EL LOGIN ES CORRECTO:
                        // 1. Creamos el intento para ir a MainActivity (Tu trabajo)
                        val intent = Intent(this, MainActivity::class.java)

                        // 2. Empaquetamos los datos para que tú los recibas allá
                        intent.putExtra("EXTRA_USER", usuario)
                        intent.putExtra("EXTRA_TIME", hora)

                        // 3. Iniciamos tu actividad y cerramos el login
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}