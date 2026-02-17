package ec.edu.uce.appproductos.controller

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression
import ec.edu.uce.appproductos.model.Producto
import ec.edu.uce.appproductos.model.ProductoDao
import ec.edu.uce.appproductos.model.Usuario
import ec.edu.uce.appproductos.model.UsuarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class ProductoViewModel(
    application: Application,
    private val productoDao: ProductoDao,
    private val usuarioDao: UsuarioDao
) : AndroidViewModel(application) {

    // ====================================================================================
    // 1. CONFIGURACIÓN E INICIALIZACIÓN
    // ====================================================================================
    private val dynamoMapper = AwsConfig.getDynamoDBMapper(application)

    // Estado de la lista de productos (La UI observa esto)
    val listaProductos: StateFlow<List<Producto>> = productoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado de Carga (Útil para mostrar Spinners en la UI)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Al iniciar la app, intentamos sincronizar y respaldar usuarios
        sincronizarYNotificar()
        subirUsuariosLocales()
    }

    // Helper para verificar conexión
    private fun hayInternet(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val cap = cm.getNetworkCapabilities(network) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ====================================================================================
    // 2. LÓGICA DE SINCRONIZACIÓN (NUBE <-> LOCAL)
    // ====================================================================================

    fun sincronizarPendientes() {
        sincronizarYNotificar()
    }

    fun descargarDeNube() {
        sincronizarYNotificar()
    }

    private fun sincronizarYNotificar() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!hayInternet()) return@launch

            // Indicamos que empezamos a cargar
            _isLoading.value = true

            try {
                // A. SUBIDA (Local -> Nube)
                val pendientes = productoDao.obtenerNoSincronizados()
                pendientes.forEach { prod ->
                    try {
                        if (prod.isDeleted) {
                            dynamoMapper.delete(prod)      // Borrar de Nube
                            productoDao.eliminar(prod)     // Limpieza Local
                        } else {
                            dynamoMapper.save(prod)        // Subir a Nube
                            prod.isSynced = true
                            productoDao.actualizar(prod)   // Marcar limpio
                        }
                    } catch (e: Exception) {
                        Log.e("SYNC", "Error sync item ${prod.codigo}: ${e.message}")
                    }
                }

                // B. DESCARGA (Nube -> Local)
                val scanResult = dynamoMapper.scan(Producto::class.java, DynamoDBScanExpression())
                scanResult.forEach { prodNube ->
                    prodNube.isSynced = true
                    prodNube.isDeleted = false
                    productoDao.insertar(prodNube)
                }

                // C. NOTIFICAR AL USUARIO (Usamos la función auxiliar)
                actualizarYNotificarTotal()

            } catch (e: Exception) {
                Log.e("SYNC", "Error general en sincronización: ${e.message}")
            } finally {
                // Siempre terminamos la carga, haya error o no
                _isLoading.value = false
            }
        }
    }

    // --- NUEVA FUNCIÓN AUXILIAR PARA EVITAR REPETIR CÓDIGO ---
    private suspend fun actualizarYNotificarTotal() {
        val total = productoDao.contarProductos()
        lanzarNotificacion(total)
    }

    private fun lanzarNotificacion(cantidad: Int) {
        val context = getApplication<Application>()
        val channelId = "sync_channel_high_priority"
        val notificationId = 1001
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android 8+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sincronización Prioritaria",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de estado de sincronización"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Inventario Actualizado")
            .setContentText("Total productos en base de datos: $cantidad")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Para Android viejo
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // Sonido y vibración
            .setAutoCancel(true)

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Log.e("NOTIF", "Permiso denegado para notificaciones.")
        }
    }

    // ====================================================================================
    // 3. CRUD DE PRODUCTOS
    // ====================================================================================

    fun guardarProducto(producto: Producto) = viewModelScope.launch(Dispatchers.IO) {
        // 1. Guardar Localmente
        producto.isSynced = false
        producto.isDeleted = false
        productoDao.insertar(producto)

        // 2. Intentar Subir (Instantáneo)
        if (hayInternet()) {
            try {
                dynamoMapper.save(producto)
                producto.isSynced = true
                productoDao.actualizar(producto)

                // --- ¡AQUÍ ESTÁ LA MAGIA! Notificamos tras guardar ---
                actualizarYNotificarTotal()

            } catch (e: Exception) {
                Log.e("AWS", "Fallo subida inmediata: ${e.message}")
            }
        }
    }

    fun eliminarProducto(producto: Producto) = viewModelScope.launch(Dispatchers.IO) {
        // 1. Soft Delete Local
        producto.isDeleted = true
        producto.isSynced = false
        productoDao.actualizar(producto)

        // 2. Intentar Borrar Nube (Instantáneo)
        if (hayInternet()) {
            try {
                dynamoMapper.delete(producto)
                productoDao.eliminar(producto) // Si se borró en nube, limpiamos local

                // --- ¡AQUÍ TAMBIÉN! Notificamos tras eliminar ---
                actualizarYNotificarTotal()

            } catch (e: Exception) {
                Log.e("AWS", "Fallo eliminado nube: ${e.message}")
            }
        }
    }

    suspend fun obtenerProductoPorCodigo(codigo: String) = productoDao.obtenerPorCodigo(codigo)


    // ====================================================================================
    // 4. GESTIÓN DE USUARIOS
    // ====================================================================================

    private fun cifrarPass(pass: String): String {
        val bytes = pass.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    suspend fun registrarUsuario(nombre: String, pass: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (usuarioDao.buscarPorNombre(nombre) != null) return@withContext false

            val passCifrada = cifrarPass(pass)
            val nuevo = Usuario(usuario = nombre, clave = passCifrada)

            // Guardar Local
            usuarioDao.insertar(nuevo)

            // Guardar Nube (Si hay internet)
            if (hayInternet()) {
                try { dynamoMapper.save(nuevo) } catch (e: Exception){ }
            }
            true
        }
    }

    suspend fun login(nombre: String, pass: String): Usuario? {
        return withContext(Dispatchers.IO) {
            val passCifrada = cifrarPass(pass)
            usuarioDao.autenticar(nombre, passCifrada)
        }
    }

    private fun subirUsuariosLocales() {
        viewModelScope.launch(Dispatchers.IO) {
            if (hayInternet()) {
                val usuarios = usuarioDao.obtenerTodos()
                usuarios.forEach { try { dynamoMapper.save(it) } catch (e: Exception){} }
            }
        }
    }

    // ====================================================================================
    // 5. VALIDACIONES
    // ====================================================================================

    fun esPasswordSegura(password: String): String? {
        if (password.length < 6) return "La contraseña debe tener al menos 6 caracteres."
        if (!password.any { it.isDigit() }) return "La contraseña debe incluir al menos un número."
        if (!password.any { it.isUpperCase() }) return "La contraseña debe incluir al menos una mayúscula."
        return null
    }

    // --- VALIDACIÓN DE USUARIO ---
    fun esUsuarioValido(usuario: String): String? {
        if (usuario.trim().length < 4) {
            return "El usuario debe tener al menos 4 caracteres."
        }
        if (usuario.contains(" ")) {
            return "El usuario no puede contener espacios."
        }
        // Opcional: Solo letras y números
        if (!usuario.all { it.isLetterOrDigit() }) {
            return "Solo use letras y números (sin símbolos)."
        }
        return null // Null = Todo correcto
    }
}

// FACTORY
class ProductoViewModelFactory(
    private val application: Application,
    private val productoDao: ProductoDao,
    private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(application, productoDao, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}