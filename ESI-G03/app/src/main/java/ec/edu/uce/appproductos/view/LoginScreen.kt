package ec.edu.uce.appproductos.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.edu.uce.appproductos.controller.Rutas
import ec.edu.uce.appproductos.model.MemoriaDatos
import ec.edu.uce.appproductos.R
import java.security.MessageDigest

@Composable
fun LoginScreen(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Colores
    val colorNaranjaTech = Color(0xFFFF9800)
    val colorRojoTech = Color(0xFFFF0000)

    // FUNCIÓN PARA GENERAR HASH SHA-256
    fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_logo_techdrop),
            contentDescription = "Logo TechDrop",
            modifier = Modifier.size(200.dp).padding(bottom = 16.dp)
        )

        Text(
            text = "TECHDROP",
            style = TextStyle(
                brush = Brush.linearGradient(listOf(colorNaranjaTech, colorRojoTech)),
                fontSize = 45.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Inputs
        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorNaranjaTech, focusedLabelColor = colorNaranjaTech)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = apellido, onValueChange = { apellido = it },
            label = { Text("Apellido (Contraseña)") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorNaranjaTech, focusedLabelColor = colorNaranjaTech)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val usuarioEncontrado = MemoriaDatos.listaUsuarios.find { user ->
                    user.nombre.equals(nombre, ignoreCase = true) &&
                            user.apellido.equals(apellido, ignoreCase = true)
                }

                if (usuarioEncontrado != null) {
                    // 1. GENERAMOS EL HASH
                    val hashGenerado = hashString(apellido)

                    // 2. NAVEGAMOS ENVIANDO: NOMBRE + APELLIDO + HASH
                    // CAMBIO AQUÍ: Agregamos '$nombre' a la ruta
                    navController.navigate("home/$nombre/$apellido/$hashGenerado") {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorNaranjaTech)
        ) {
            Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate(Rutas.REGISTRO) }) {
            Text("¿No tienes cuenta? Regístrate aquí", color = colorNaranjaTech, fontWeight = FontWeight.Bold)
        }
    }
}