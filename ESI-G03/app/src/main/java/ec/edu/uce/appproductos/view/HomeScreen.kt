package ec.edu.uce.appproductos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.edu.uce.appproductos.controller.Rutas
import ec.edu.uce.appproductos.model.MemoriaDatos
import ec.edu.uce.appproductos.model.Producto

// 1. RECIBIMOS EL NOMBRE AQUÍ
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    nombreUsuario: String, // <--- Nuevo parámetro
    passwordRecibida: String,
    hashRecibido: String
) {

    var mostrarDialogoBorrar by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    var mostrarInfoHash by remember { mutableStateOf(true) }

    // (El bloque AlertDialog de Hash sigue igual...)
    if (mostrarInfoHash) {
        AlertDialog(
            onDismissRequest = { mostrarInfoHash = false },
            title = { Text("Seguridad - Datos de Sesión") },
            text = { Text("Usuario: $nombreUsuario\nPass: $passwordRecibida\nHash: $hashRecibido") },
            confirmButton = { TextButton(onClick = { mostrarInfoHash = false }) { Text("Entendido") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TechDrop Productos") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("formulario_producto/nuevo") },
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // 2. CAMPO DE TEXTO CON EL NOMBRE DEL USUARIO (Tarjeta bonita)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Bienvenido,", fontSize = 14.sp)
                        Text(
                            text = nombreUsuario, // <--- AQUÍ MOSTRAMOS EL NOMBRE
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 3. LA LISTA DE PRODUCTOS (Ahora dentro de la Columna)
            LazyColumn {
                items(MemoriaDatos.listaProductos) { producto ->
                    ItemProducto(
                        producto = producto,
                        onEditClick = { navController.navigate("formulario_producto/${producto.id}") },
                        onDeleteClick = {
                            productoAEliminar = producto
                            mostrarDialogoBorrar = true
                        }
                    )
                }
            }
        }

        // (El AlertDialog de Borrar sigue igual...)
        if (mostrarDialogoBorrar && productoAEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoBorrar = false },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Eliminar '${productoAEliminar?.descripcion}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        MemoriaDatos.listaProductos.remove(productoAEliminar)
                        mostrarDialogoBorrar = false
                    }) { Text("Eliminar", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoBorrar = false }) { Text("Cancelar") }
                }
            )
        }
    }
}