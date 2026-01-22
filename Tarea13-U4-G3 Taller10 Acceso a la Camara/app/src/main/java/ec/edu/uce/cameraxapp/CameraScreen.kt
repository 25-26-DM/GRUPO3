package ec.edu.uce.cameraxapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import ec.edu.uce.cameraxapp.ui.auth.AuthViewModel
import ec.edu.uce.cameraxapp.ui.theme.AccesoCamaraTheme
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import androidx.compose.ui.tooling.preview.Preview as ComposePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(modifier: Modifier = Modifier, authViewModel: AuthViewModel, navController: NavController) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentUser by authViewModel.currentUser.collectAsState()

    // Se recuerdan TODOS los casos de uso para que persistan
    val preview = remember { Preview.Builder().build() }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val imageAnalysis = remember { ImageAnalysis.Builder().build() }
    val videoCapture = remember {
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        VideoCapture.withOutput(recorder)
    }

    var recording: Recording? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isPhotoMode by remember { mutableStateOf(true) }
    var lastMediaUri by remember { mutableStateOf<Uri?>(null) }
    var luma by remember { mutableStateOf(0.0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Este efecto solo se encarga de VINCULAR los casos de uso
    LaunchedEffect(lensFacing, isPhotoMode) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Se configuran los casos de uso antes de vincularlos
            preview.setSurfaceProvider(previewView.surfaceProvider)
            imageAnalysis.setAnalyzer(cameraExecutor, LuminosityAnalyzer { l ->
                luma = l
            })

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                if (isPhotoMode) {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalysis
                    )
                } else {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, videoCapture, imageAnalysis
                    )
                }
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirmar Cierre de Sesión") },
            text = { Text("${currentUser?.username}, ¿estás seguro de salir de CámaraXapp?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_grupo),
                            contentDescription = "Logo del Grupo",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cámara Grupo3")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("gallery") }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Abrir galería")
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = String.format(Locale.US, "Luminosidad: %.2f", luma),
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                color = Color.White
            )

            // Grupo de botones de acción
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Botón para cambiar entre foto y video
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = { isPhotoMode = !isPhotoMode },
                    ) {
                        Icon(if (isPhotoMode) Icons.Default.Videocam else Icons.Default.CameraAlt, contentDescription = "Cambiar modo")
                    }
                    val text = if (isPhotoMode) "Cambiar a Video" else "Cambiar a Foto"
                    Text(text = text, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                }

                // Botón principal (foto o video)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (isPhotoMode) {
                                takePhoto(imageCapture, context) { uri -> lastMediaUri = uri }
                            } else {
                                recording = captureVideo(videoCapture, context, recording) { uri -> lastMediaUri = uri }
                            }
                        }
                    ) {
                        val icon = if (isPhotoMode) Icons.Default.CameraAlt else if (recording != null) Icons.Default.Stop else Icons.Default.Videocam
                        Icon(icon, contentDescription = if (isPhotoMode) "Tomar foto" else "Grabar video")
                    }
                    val text = if (isPhotoMode) "Foto" else if (recording != null) "Detener" else "Grabar"
                    Text(text = text, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                }

                // Botón para cambiar de cámara
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = {
                            lensFacing =
                                if (lensFacing == CameraSelector.LENS_FACING_BACK)
                                    CameraSelector.LENS_FACING_FRONT
                                else
                                    CameraSelector.LENS_FACING_BACK
                        }
                    ) {
                        Icon(Icons.Default.Cached, contentDescription = "Girar cámara")
                    }
                    Text(text = "Girar Cámara", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}


fun takePhoto(
    imageCapture: ImageCapture?,
    context: Context,
    onImageSaved: (Uri) -> Unit
) {
    val capture = imageCapture ?: return

    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraXApp")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    capture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                exc.printStackTrace()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: return
                onImageSaved(savedUri)
                Toast.makeText(context, "📸 Foto guardada", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

fun captureVideo(videoCapture: VideoCapture<Recorder>?, context: Context, recording: Recording?, onVideoSaved: (Uri) -> Unit): Recording? {
    val currentRecording = recording
    if (currentRecording != null) {
        currentRecording.stop()
        return null
    }

    val capture = videoCapture ?: return null

    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
    }

    val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        .setContentValues(contentValues)
        .build()

    return capture.output.prepareRecording(context, mediaStoreOutputOptions)
        .withAudioEnabled() // <-- ¡AQUÍ ESTÁ LA MAGIA!
        .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> {
                    Toast.makeText(context, "Grabación iniciada", Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Finalize -> {
                    if (!recordEvent.hasError()) {
                        val savedUri = recordEvent.outputResults.outputUri
                        onVideoSaved(savedUri)
                        val msg = "Video guardado: $savedUri"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    } else {
                        recording?.close()
                        Toast.makeText(context, "Error al grabar: ${recordEvent.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
}

@ComposePreview(showBackground = true)
@Composable
fun CameraPreview() {
    // No se puede previsualizar porque requiere el AuthViewModel y NavController
    // AccesoCamaraTheme {
    //     CameraScreen()
    // }
}
