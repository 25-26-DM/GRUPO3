package ec.edu.uce.appproductos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ec.edu.uce.appproductos.controller.AppNavigation
import ec.edu.uce.appproductos.controller.ProductoViewModel
import ec.edu.uce.appproductos.controller.ProductoViewModelFactory
import ec.edu.uce.appproductos.controller.Rutas
import ec.edu.uce.appproductos.controller.SessionManager
import ec.edu.uce.appproductos.model.AppDatabase
import ec.edu.uce.appproductos.ui.theme.TAREA07U2G3AppProductosTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    // Detector de inactividad
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        SessionManager.registrarActividad()
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instancias de BD
        val database = AppDatabase.getDatabase(this)
        val productoDao = database.productoDao()
        val usuarioDao = database.usuarioDao()

        setContent {
            TAREA07U2G3AppProductosTheme {

                val context = LocalContext.current

                // 1. --- SOLICITUD DE PERMISO DE NOTIFICACIONES (Android 13+) ---
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (isGranted) {
                            Toast.makeText(context, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Sin permiso no verás avisos de sincronización", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                // Verifica el permiso cada vez que se inicia la pantalla
                SideEffect {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permiso = Manifest.permission.POST_NOTIFICATIONS
                        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
                            launcher.launch(permiso)
                        }
                    }
                }
                // ---------------------------------------------------------------

                val viewModel: ProductoViewModel = viewModel(
                    factory = ProductoViewModelFactory(application, productoDao, usuarioDao)
                )
                val navController = rememberNavController()

                // 2. SINCRONIZACIÓN AL INICIAR
                LaunchedEffect(Unit) {
                    viewModel.sincronizarPendientes()
                }

                // 3. VIGILANTE DE RED EN TIEMPO REAL
                DisposableEffect(Unit) {
                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                    val networkCallback = object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            super.onAvailable(network)
                            viewModel.sincronizarPendientes()
                        }
                    }

                    val request = NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()

                    connectivityManager.registerNetworkCallback(request, networkCallback)

                    onDispose {
                        connectivityManager.unregisterNetworkCallback(networkCallback)
                    }
                }

                // 4. VIGILANTE DE SESIÓN
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000)
                        val errorSesion = SessionManager.verificarEstadoSesion()

                        if (errorSesion != null) {
                            SessionManager.cerrarSesion()
                            Toast.makeText(applicationContext, errorSesion, Toast.LENGTH_LONG).show()
                            navController.navigate(Rutas.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                // 5. Navegación
                AppNavigation(navController = navController, viewModel = viewModel)
            }
        }
    }
}