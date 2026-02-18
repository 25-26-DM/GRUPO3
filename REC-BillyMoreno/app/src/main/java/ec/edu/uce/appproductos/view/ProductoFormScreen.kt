package ec.edu.uce.appproductos.view

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ec.edu.uce.appproductos.Utils
import ec.edu.uce.appproductos.controller.ProductoViewModel
import ec.edu.uce.appproductos.model.Producto
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    navController: NavController,
    idProducto: String?,
    viewModel: ProductoViewModel
) {
    val context = LocalContext.current
    val esEdicion = idProducto != null && idProducto != "nuevo"
    val scrollState = rememberScrollState()

    // Colores de marca
    val colorNaranja = Color(0xFFFF9800)
    val colorRojo = Color(0xFFFF0000)
    val gradienteBoton = Brush.horizontalGradient(listOf(colorNaranja, colorRojo))

    // --- ESTADOS DEL FORMULARIO ---
    var codigo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    // Fecha ahora guarda milisegundos internamente para el calendario
    var fechaMilis by remember { mutableStateOf<Long?>(null) }
    var costo by remember { mutableStateOf("") }
    var isDisponible by remember { mutableStateOf(true) } // Por defecto disponible

    // --- ESTADOS PARA FOTO Y CROP ---
    var fotoUriFinal by remember { mutableStateOf<Uri?>(null) } // La que se muestra
    var fotoUriTemporalCogi by remember { mutableStateOf<Uri?>(null) } // La que toma la cámara

    // --- ESTADOS PARA DATE PICKER ---
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()


    // 1. LÓGICA DE INICIO (Cargar datos o Generar Código)
    LaunchedEffect(Unit) {
        if (esEdicion && idProducto != null) {
            // MODO EDICIÓN: Cargar datos
            val producto = viewModel.obtenerProductoPorCodigo(idProducto)
            producto?.let {
                codigo = it.codigo
                descripcion = it.descripcion
                // Convertimos el string de fecha a milisegundos si es posible
                try {
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    fechaMilis = sdf.parse(it.fechaFabricacion)?.time
                } catch (e: Exception) { /* Error al parsear fecha antigua */ }

                costo = it.costo.toString()
                isDisponible = it.isDisponible
                if (it.fotoPath != null) fotoUriFinal = Uri.parse(it.fotoPath)
            }
        } else {
            // MODO NUEVO: Generar código automático
            codigo = Utils.generarCodigoProducto()
            // Fecha por defecto hoy
            fechaMilis = System.currentTimeMillis()
        }
    }

    // 2. CONFIGURACIÓN DE LAUNDERS (Cámara y Crop)

    // B. Lanzador para el RECORTE (uCrop)
    val cropLauncher = rememberLauncherForActivityResult(contract = CropImageContract()) { uriRecortada ->
        if (uriRecortada != null) {
            // ¡Éxito! La foto recortada es la final
            fotoUriFinal = uriRecortada
        } else {
            Toast.makeText(context, "Recorte cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    // A. Lanzador para la CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && fotoUriTemporalCogi != null) {
            // Si la foto se tomó bien, LANZAMOS EL RECORTE inmediatamente
            val uriDestino = crearArchivoDestinoCrop(context)
            cropLauncher.launch(Pair(fotoUriTemporalCogi!!, uriDestino))
        }
    }

    // Función para iniciar el proceso de foto
    fun iniciarProcesoFoto() {
        // 1. Crear archivo temporal para la cámara
        val archivoTmp = crearArchivoImagen(context)
        val uriTmp = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            "${context.packageName}.fileprovider",
            archivoTmp
        )
        fotoUriTemporalCogi = uriTmp // Guardamos la referencia
        // 2. Abrir cámara
        cameraLauncher.launch(uriTmp)
    }


    // --- INTERFAZ DE USUARIO ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (esEdicion) "Editar Producto" else "Registrar Producto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState) // Permite scroll si la pantalla es pequeña
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- SECCIÓN FOTO MEJORADA ---
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { iniciarProcesoFoto() }, // Click para iniciar cámara -> crop
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (fotoUriFinal != null) {
                        // Muestra la foto final (recortada o cargada)
                        Image(
                            painter = rememberAsyncImagePainter(fotoUriFinal),
                            contentDescription = "Foto Producto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Icono pequeño de edición encima
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(40.dp)
                                .background(colorNaranja, CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                        }
                    } else {
                        // Estado vacío
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = colorNaranja,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tocar para agregar foto", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CAMPOS DEL FORMULARIO ---

            // 1. CÓDIGO (Solo lectura, generado auto)
            OutlinedTextField(
                value = codigo,
                onValueChange = { }, // No hace nada, es read-only
                label = { Text("Código (Automático)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // <--- IMPORTANTE
                enabled = false, // Se ve desactivado pero legible
                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = colorNaranja
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2. DESCRIPCIÓN
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del Producto") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = outlinedFieldColorsMarca(colorNaranja)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 3. FECHA (Con Calendario Flotante)
            OutlinedTextField(
                value = Utils.formatearFechaMilis(fechaMilis), // Muestra la fecha formateada
                onValueChange = { }, // Read-only
                label = { Text("Fecha de Fabricación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarCalendario = true }, // Al click, abre dialog
                readOnly = true,
                enabled = false, // Desactivado para que el click lo maneje el modifier
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = colorNaranja,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 4. COSTO
            OutlinedTextField(
                value = costo,
                onValueChange = { costo = it },
                label = { Text("Costo ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = outlinedFieldColorsMarca(colorNaranja)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. DISPONIBILIDAD (Switch mejorado)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("¿Está disponible para venta?", fontWeight = FontWeight.Medium)
                Switch(
                    checked = isDisponible,
                    onCheckedChange = { isDisponible = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = colorNaranja, checkedTrackColor = colorNaranja.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = {
                    // Validación básica
                    if (descripcion.isBlank() || costo.isBlank() || fechaMilis == null) {
                        Toast.makeText(context, "Por favor complete la descripción, costo y fecha", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val precioDouble = costo.toDoubleOrNull() ?: 0.0
                    val fechaTexto = Utils.formatearFechaMilis(fechaMilis) // Guardamos como texto final

                    // Crear objeto Producto
                    val producto = Producto(
                        codigo = codigo,
                        descripcion = descripcion,
                        fechaFabricacion = fechaTexto,
                        costo = precioDouble,
                        isDisponible = isDisponible,
                        fotoPath = fotoUriFinal?.toString() // Guardamos la ruta final (recortada)
                    )

                    viewModel.guardarProducto(producto)
                    Toast.makeText(context, "Producto procesado con éxito", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradienteBoton),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GUARDAR PRODUCTO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }

        // --- DIÁLOGO DEL CALENDARIO (Se muestra si mostrarCalendario es true) ---
        if (mostrarCalendario) {
            DatePickerDialog(
                onDismissRequest = { mostrarCalendario = false },
                confirmButton = {
                    TextButton(onClick = {
                        // Al confirmar, guardamos los milisegundos seleccionados
                        fechaMilis = datePickerState.selectedDateMillis
                        mostrarCalendario = false
                    }) {
                        Text("Aceptar", color = colorNaranja)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarCalendario = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

// --- FUNCIONES AUXILIARES DE UI ---

@Composable
fun outlinedFieldColorsMarca(colorMarca: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colorMarca,
    focusedLabelColor = colorMarca,
    focusedLeadingIconColor = colorMarca,
    cursorColor = colorMarca
)

// Mantenemos la función original para crear el archivo de la cámara
fun crearArchivoImagen(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val nombreImagen = "JPEG_" + timeStamp + "_"
    val directorio = context.getExternalFilesDir(null)
    return File.createTempFile(nombreImagen, ".jpg", directorio)
}