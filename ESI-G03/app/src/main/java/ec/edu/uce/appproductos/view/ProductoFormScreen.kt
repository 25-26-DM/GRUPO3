package ec.edu.uce.appproductos.view


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.edu.uce.appproductos.model.MemoriaDatos
import ec.edu.uce.appproductos.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(navController: NavController, idProducto: String?) {

    val context = LocalContext.current
    val esEdicion = idProducto != null && idProducto != "nuevo"

    // Estados de los campos
    var codigo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var disponible by remember { mutableStateOf(false) }

    // Bloque LaunchedEffect: Se ejecuta una sola vez al entrar a la pantalla
    LaunchedEffect(Unit) {
        if (esEdicion) {
            // Buscamos el producto en la memoria
            val producto = MemoriaDatos.listaProductos.find { it.id == idProducto }
            producto?.let {
                codigo = it.id
                descripcion = it.descripcion
                fecha = it.fechaFabricacion
                costo = it.costo.toString()
                disponible = it.disponible
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Producto" else "Nuevo Producto") },
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
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo Código (Solo editable si es nuevo, usualmente el ID no se cambia)
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código (ID)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !esEdicion // Si editamos, bloqueamos el ID para no romper referencias
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha Fabricación (dd/mm/aaaa)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = costo,
                onValueChange = { costo = it },
                label = { Text("Costo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox para Disponibilidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = disponible, onCheckedChange = { disponible = it })
                Text("¿Está disponible?")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // VALIDACIÓN SIMPLE
                    if (codigo.isBlank() || descripcion.isBlank() || costo.isBlank()) {
                        Toast.makeText(context, "Llene todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val precioDouble = costo.toDoubleOrNull() ?: 0.0

                    if (esEdicion) {
                        // MODO EDICIÓN:
                        // 1. Buscamos el índice en la lista
                        val index = MemoriaDatos.listaProductos.indexOfFirst { it.id == idProducto }
                        if (index != -1) {
                            // 2. Reemplazamos el objeto para que la lista reaccione (Compose detecta el cambio)
                            MemoriaDatos.listaProductos[index] = Producto(
                                id = codigo,
                                descripcion = descripcion,
                                fechaFabricacion = fecha,
                                costo = precioDouble,
                                disponible = disponible
                            )
                            Toast.makeText(context, "Producto Editado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // MODO CREACIÓN:
                        // 1. Validar que el ID no exista ya
                        if (MemoriaDatos.listaProductos.any { it.id == codigo }) {
                            Toast.makeText(context, "El Código ya existe", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // 2. Agregamos a la lista
                        MemoriaDatos.listaProductos.add(
                            Producto(codigo, descripcion, fecha, precioDouble, disponible)
                        )
                        Toast.makeText(context, "Producto Creado", Toast.LENGTH_SHORT).show()
                    }

                    // Volver al Home
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}