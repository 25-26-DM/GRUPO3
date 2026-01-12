/*
 * Copyright (C) 2023 The Android Open Source Project
 */

package ec.edu.uce.marsphotos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ec.edu.uce.marsphotos.ui.MarsPhotosApp
import ec.edu.uce.marsphotos.ui.theme.MarsPhotosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // --- CAMBIO 1 (TU ROL): RECIBIR DATOS DEL INTENT ---
        // Aquí atrapamos lo que nos envía el LoginActivity
        val usuarioRecibido = intent.getStringExtra("EXTRA_USER") ?: "Usuario Invitado"
        val horaRecibida = intent.getStringExtra("EXTRA_TIME") ?: ""

        setContent {
            MarsPhotosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // --- CAMBIO 2: PASAR DATOS A LA APP ---
                    // IMPORTANTE: Esto marcará error en rojo hasta que actualices
                    // el archivo MarsPhotosApp.kt con el código que te pasé antes.
                    MarsPhotosApp(
                        usuario = usuarioRecibido,
                        hora = horaRecibida
                    )
                }
            }
        }
    }
}