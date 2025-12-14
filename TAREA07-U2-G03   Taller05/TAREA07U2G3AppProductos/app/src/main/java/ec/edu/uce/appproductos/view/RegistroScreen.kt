package ec.edu.uce.appproductos.view


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.edu.uce.appproductos.model.MemoriaDatos
import ec.edu.uce.appproductos.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {

    // Estado del formulario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Usuario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Complete sus datos", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Guardar
            Button(
                onClick = {
                    // VALIDACIÓN SIMPLE
                    if (nombre.isBlank() || apellido.isBlank()) {
                        Toast.makeText(context, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
                    } else {
                        // 1. CREAR el objeto Usuario (MODELO)
                        val nuevoUsuario = Usuario(nombre.trim(), apellido.trim())

                        // 2. GUARDAR en memoria (PERSISTENCIA)
                        MemoriaDatos.listaUsuarios.add(nuevoUsuario)

                        // 3. Feedback y Navegación
                        Toast.makeText(context, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // Volvemos al Login para que pruebe ingresar
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}