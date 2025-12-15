package ec.edu.uce.appproductos.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importante para remember y mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.edu.uce.appproductos.controller.Rutas
import ec.edu.uce.appproductos.model.MemoriaDatos
import ec.edu.uce.appproductos.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    // 1. ESTADOS PARA CONTROLAR EL DIÁLOGO DE CONFIRMACIÓN
    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Productos") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.HOME) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("formulario_producto/nuevo") },
                containerColor = Color(0xFFFF9800), // Naranja TechDrop
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(MemoriaDatos.listaProductos) { producto ->
                ItemProducto(
                    producto = producto,
                    onEditClick = {
                        navController.navigate("formulario_producto/${producto.id}")
                    },
                    onDeleteClick = {
                        // 2. EN LUGAR DE BORRAR, ACTIVAMOS EL DIÁLOGO
                        productoAEliminar = producto
                        mostrarDialogo = true
                    }
                )
            }
        }

        // 3. EL COMPONENTE ALERT DIALOG
        if (mostrarDialogo && productoAEliminar != null) {
            AlertDialog(
                onDismissRequest = {
                    // Si hace clic fuera, se cierra
                    mostrarDialogo = false
                    productoAEliminar = null
                },
                title = { Text(text = "Confirmar Eliminación") },
                text = {
                    Text("¿Seguro desea eliminar el producto '${productoAEliminar?.descripcion}'?")
                },
                confirmButton = {
                    // BOTÓN CONFIRMAR (ROJO)
                    TextButton(
                        onClick = {
                            // Aquí ocurre la magia del borrado
                            MemoriaDatos.listaProductos.remove(productoAEliminar)
                            mostrarDialogo = false
                            productoAEliminar = null
                        }
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    // BOTÓN CANCELAR
                    TextButton(
                        onClick = {
                            mostrarDialogo = false
                            productoAEliminar = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}