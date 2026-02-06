package ec.edu.uce.appproductos.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import ec.edu.uce.appproductos.R
import ec.edu.uce.appproductos.controller.ProductoViewModel
import ec.edu.uce.appproductos.controller.Rutas
import ec.edu.uce.appproductos.controller.SessionManager
import ec.edu.uce.appproductos.ui.theme.PlayfairDisplay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: ProductoViewModel
) {
    // --- ESTADOS ---
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // --- ESTADOS DE SEGURIDAD ---
    var intentosFallidos by remember { mutableIntStateOf(0) }
    val imageCapture = remember { ImageCapture.Builder().build() } // Controlador de captura

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // --- CONFIGURACIÓN DE CÁMARA SILENCIOSA ---
    // Pedimos permiso al iniciar el Login para que la cámara esté lista
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            iniciarCamaraFrontal(context, lifecycleOwner, imageCapture)
        }
    }

    LaunchedEffect(Unit) {
        // Verificar permiso
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            iniciarCamaraFrontal(context, lifecycleOwner, imageCapture)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // --- COLORES Y ESTILOS ---
    val fondoOscuro = Color(0xFF121212)
    val colorNaranjaTech = Color(0xFFFF9800)
    val colorRojoTech = Color(0xFFFF0000)
    val colorInputFondo = Color(0xFF1E1E1E)
    val gradienteMarca = Brush.horizontalGradient(listOf(colorNaranjaTech, colorRojoTech))

    Surface(modifier = Modifier.fillMaxSize(), color = fondoOscuro) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // LOGO
            Box(
                modifier = Modifier.size(160.dp)
                    .background(Brush.radialGradient(listOf(colorNaranjaTech.copy(alpha = 0.2f), Color.Transparent)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = R.drawable.ic_logo_techdrop), contentDescription = null, modifier = Modifier.size(140.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("TECHDROP", style = TextStyle(brush = gradienteMarca, fontSize = 40.sp, fontWeight = FontWeight.Bold, fontFamily = PlayfairDisplay, letterSpacing = 3.sp))
            Text("Bienvenido de nuevo", color = Color.Gray, fontSize = 16.sp, fontFamily = PlayfairDisplay, modifier = Modifier.padding(bottom = 32.dp))

            // INPUTS
            OutlinedTextField(
                value = usuario, onValueChange = { usuario = it },
                label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Person, null, tint = colorNaranjaTech) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorNaranjaTech, unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = colorNaranjaTech, unfocusedLabelColor = Color.Gray,
                    cursorColor = colorNaranjaTech, focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = colorInputFondo, unfocusedContainerColor = colorInputFondo
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = colorNaranjaTech) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = Color.Gray)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorNaranjaTech, unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = colorNaranjaTech, unfocusedLabelColor = Color.Gray,
                    cursorColor = colorNaranjaTech, focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = colorInputFondo, unfocusedContainerColor = colorInputFondo
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN INGRESAR CON LÓGICA DE SEGURIDAD
            Button(
                onClick = {
                    scope.launch {
                        val usuarioEncontrado = viewModel.login(usuario, password)

                        if (usuarioEncontrado != null) {
                            // ÉXITO: Reiniciamos contador y entramos
                            intentosFallidos = 0
                            SessionManager.iniciarSesion()
                            navController.navigate("home/${usuarioEncontrado.usuario}/$password/${usuarioEncontrado.clave}") {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        } else {
                            // FALLO
                            intentosFallidos++
                            Toast.makeText(context, "Credenciales Incorrectas ($intentosFallidos/3)", Toast.LENGTH_SHORT).show()

                            // VERIFICAR SI LLEGÓ AL LÍMITE
                            if (intentosFallidos >= 3) {
                                tomarFotoIntruso(context, imageCapture)
                                Toast.makeText(context, "⚠️ ALERTA DE SEGURIDAD: Foto capturada", Toast.LENGTH_LONG).show()
                                intentosFallidos = 0 // Reiniciamos o bloqueamos (aquí reiniciamos ciclo)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(modifier = Modifier.fillMaxSize().background(gradienteMarca), contentAlignment = Alignment.Center) {
                    Text("INGRESAR", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = PlayfairDisplay)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta?", color = Color.Gray, fontSize = 14.sp)
                TextButton(onClick = { navController.navigate(Rutas.REGISTRO) }) {
                    Text("Regístrate aquí", color = colorNaranjaTech, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// --- FUNCIONES AUXILIARES DE CÁMARA ---

// 1. Configurar la cámara para que esté lista (BIND)
fun iniciarCamaraFrontal(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    imageCapture: ImageCapture
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // Seleccionamos cámara FRONTAL
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            // Vinculamos la cámara al ciclo de vida del LoginScreen
            // No necesitamos Preview, solo ImageCapture para tomar la foto
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraX", "Fallo al vincular cámara", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

// 2. Tomar la foto (SHOOT)
fun tomarFotoIntruso(context: Context, imageCapture: ImageCapture) {
    // Crear archivo con nombre de "INTRUSO"
    val name = "INTRUSO_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        // Guardar en imágenes públicas para que puedas verla en la galería fácilmente y comprobar que funciona
        put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TechDrop-Security")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraX", "Fallo captura intruso: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d("CameraX", "¡Foto de intruso capturada exitosamente!")
            }
        }
    )
}