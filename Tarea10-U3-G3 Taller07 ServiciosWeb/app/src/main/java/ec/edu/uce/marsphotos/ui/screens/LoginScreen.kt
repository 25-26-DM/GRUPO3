package ec.edu.uce.marsphotos.ui.screens // Asegúrate que este paquete coincida con el tuyo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ec.edu.uce.marsphotos.R // Importa R para acceder a tu logo
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit, // Función para avisar que entramos
    modifier: Modifier = Modifier
) {
    // --- LISTA DE USUARIOS VÁLIDOS (Memoria Local) ---
    // El nombre es la clave, el apellido es la contraseña.
    val credenciales = remember {
        mapOf(
            "Damian" to "Minda",
            "David" to "Ortega",
            "Alexis" to "Carvajal",
            "Joel" to "Guamangallo",
            "Jostyn" to "Palacios",
            "Billy" to "Moreno"
        )
    }

    //Variables de estado
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    // var error by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // <-- Esto jala el color del tema (Negro o Blanco)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. EL LOGO (Asegúrate de tener una imagen en res/drawable)
            Image(
                painter = painterResource(id = R.drawable.logo_grupo), // Cambia por tu logo
                contentDescription = "Logo Tech Drop",
                modifier = Modifier.size(200.dp).padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Inicio de Sesión",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. CAMPO USUARIO (Nombre)
            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    mensajeError = null // Limpiar error al escribir
                },
                label = { Text("Nombre de Usuario") },
                isError = mensajeError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. CAMPO CONTRASEÑA (Apellido)
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    mensajeError = null // Limpiar error al escribir
                },
                label = { Text("Contraseña (Apellido)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Oculta el texto con puntos
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = mensajeError != null
            )

            // Mensaje de error en rojo si falla la validación
            if (mensajeError != null) {
                Text(
                    text = mensajeError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. BOTÓN CON VALIDACIÓN
            Button(
                onClick = {
                    // Lógica de validación
                    val contrasenaCorrecta =
                        credenciales[usuario] // Busca el apellido para ese nombre

                    if (contrasenaCorrecta != null && contrasenaCorrecta == password) {
                        // ¡Login exitoso!
                        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val horaActual = sdf.format(Date())
                        onLoginSuccess(usuario, horaActual)
                    } else {
                        // Falló: Usuario no existe o contraseña incorrecta
                        mensajeError = "Usuario o contraseña incorrectos. Verifica tus datos."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = usuario.isNotBlank() && password.isNotBlank() // Se desactiva si están vacíos
            ) {
                Text("Ingresar", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}