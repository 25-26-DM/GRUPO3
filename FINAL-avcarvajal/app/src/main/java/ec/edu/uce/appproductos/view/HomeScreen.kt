package ec.edu.uce.appproductos.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ec.edu.uce.appproductos.controller.ProductoViewModel
import ec.edu.uce.appproductos.controller.Rutas
import ec.edu.uce.appproductos.controller.SessionManager
import ec.edu.uce.appproductos.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    nombreUsuario: String,
    pass: String, // Se mantienen por compatibilidad con la navegación
    hash: String,
    viewModel: ProductoViewModel
) {
    // 1. OBSERVAMOS DATOS Y ESTADOS
    val listaProductos by viewModel.listaProductos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // <--- ¡Nuevo estado de carga!

    var mostrarDialogoBorrar by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    val context = LocalContext.current

    // Mensaje de bienvenida único
    LaunchedEffect(Unit) {
        Toast.makeText(context, "Bienvenido, $nombreUsuario", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("TechDrop Inventario", fontWeight = FontWeight.Bold)
                        Text("Sede Central", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    }
                },
                actions = {
                    // BOTÓN CERRAR SESIÓN
                    IconButton(onClick = {
                        SessionManager.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
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

        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            Column(modifier = Modifier.padding(16.dp)) {

                // TARJETA DE USUARIO
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Usuario Activo", fontSize = 12.sp)
                            Text(nombreUsuario, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mis Productos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${listaProductos.size} items", fontSize = 14.sp, color = Color.Gray)
                }

                // --- LÓGICA DE VISUALIZACIÓN ---
                if (listaProductos.isEmpty() && !isLoading) {
                    // CASO A: EMPTY STATE (Lista vacía)
                    Column(
                        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp), // Padding para no chocar con el FAB
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No tienes productos registrados", color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text("Pulsa el botón + para comenzar", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    // CASO B: LISTA DE PRODUCTOS
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(listaProductos) { producto ->
                            ItemProductoCompleto(
                                producto = producto,
                                onEditClick = { navController.navigate("formulario_producto/${producto.codigo}") },
                                onDeleteClick = {
                                    productoAEliminar = producto
                                    mostrarDialogoBorrar = true
                                }
                            )
                        }
                    }
                }
            }

            // CASO C: ESTADO DE CARGA (Overlay)
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)), // Fondo oscurecido
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF9800))
                }
            }
        }

        // DIÁLOGO DE CONFIRMACIÓN
        if (mostrarDialogoBorrar && productoAEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoBorrar = false },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de eliminar '${productoAEliminar?.descripcion}'? Esta acción se sincronizará con la nube.") },
                confirmButton = {
                    Button(
                        onClick = {
                            productoAEliminar?.let { viewModel.eliminarProducto(it) }
                            mostrarDialogoBorrar = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { mostrarDialogoBorrar = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun ItemProductoCompleto(
    producto: Producto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. FOTO
            if (producto.fotoPath != null) {
                Image(
                    painter = rememberAsyncImagePainter(producto.fotoPath),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, null, tint = Color.Gray)
                        Text("Sin Foto", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. DATOS
            Column(modifier = Modifier.weight(1f)) {
                // Título y Estado Sync
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = producto.descripcion,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    // INDICADOR DE SYNC VISUAL
                    if (producto.isSynced) {
                        Icon(Icons.Default.Cloud, contentDescription = "Sincronizado", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Default.CloudOff, contentDescription = "Pendiente", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Cód: ${producto.codigo}", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Fab: ${producto.fechaFabricacion}", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${producto.costo}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF2E7D32) // Verde oscuro dinero
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Chip de estado pequeño
                    val (colorFondo, textoEstado) = if (producto.isDisponible) {
                        Pair(Color(0xFFE8F5E9), "Disponible") // Verde claro
                    } else {
                        Pair(Color(0xFFFFEBEE), "Agotado") // Rojo claro
                    }

                    Surface(
                        color = colorFondo,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = textoEstado,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = if(producto.isDisponible) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }

            // 3. ACCIONES
            Column {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, null, tint = Color(0xFF1976D2))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFD32F2F))
                }
            }
        }
    }
}