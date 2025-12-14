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

@Composable
fun LoginScreen(navController: NavController) {

    // ESTADO
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Definimos los colores de tu paleta aquí para usarlos fácil
    val colorNaranjaTech = Color(0xFFFF9800)
    val colorRojoTech = Color(0xFFFF0000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 1. LOGO
        Image(
            painter = painterResource(id = R.drawable.ic_logo_techdrop),
            contentDescription = "Logo TechDrop",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )

        // 2. TÍTULO CON DEGRADADO
        Text(
            text = "TECHDROP",
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(colorNaranjaTech, colorRojoTech)
                ),
                fontSize = 45.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Inputs
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // Opcional: Hacer que el borde activo también sea naranja
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorNaranjaTech,
                focusedLabelColor = colorNaranjaTech
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorNaranjaTech,
                focusedLabelColor = colorNaranjaTech
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. BOTÓN "INGRESAR" CON COLOR DE PALETA
        Button(
            onClick = {
                val usuarioEncontrado = MemoriaDatos.listaUsuarios.find { user ->
                    user.nombre.equals(nombre, ignoreCase = true) &&
                            user.apellido.equals(apellido, ignoreCase = true)
                }

                if (usuarioEncontrado != null) {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // AQUI ESTÁ EL CAMBIO DE COLOR DEL BOTÓN
            colors = ButtonDefaults.buttonColors(
                containerColor = colorNaranjaTech, // El naranja de tu marca
                contentColor = Color.White // Texto blanco para contraste
            )
        ) {
            Text(
                text = "Ingresar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. TEXTO DE REGISTRO CON COLOR DE PALETA
        TextButton(onClick = {
            navController.navigate(Rutas.REGISTRO)
        }) {
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                // AQUI ESTÁ EL CAMBIO DE COLOR DEL TEXTO
                color = colorNaranjaTech,
                fontWeight = FontWeight.Bold
            )
        }
    }
}