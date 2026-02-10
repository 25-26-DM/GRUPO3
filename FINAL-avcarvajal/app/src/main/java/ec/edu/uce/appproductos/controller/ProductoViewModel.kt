package ec.edu.uce.appproductos.controller

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
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

    // Estado de la lista de productos
    val listaProductos: StateFlow<List<Producto>> = productoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado de Carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ACTUALIZACIÓN: Estado para el mensaje de error de msgdrop
    private val _errorMsgDrop = MutableStateFlow<String?>(null)
    val errorMsgDrop: StateFlow<String?> = _errorMsgDrop.asStateFlow()

    init {
        sincronizarYNotificar()
        subirUsuariosLocales()
    }

    fun mensajeMostrado() {
        _errorMsgDrop.value = null
    }

    private fun hayInternet(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val cap = cm.getNetworkCapabilities(network) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ====================================================================================
    // 2. LÓGICA DE SINCRONIZACIÓN (NUBE <-> LOCAL)
    // ====================================================================================

    fun sincronizarPendientes() { sincronizarYNotificar() }
    fun descargarDeNube() { sincronizarYNotificar() }

    private fun sincronizarYNotificar() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!hayInternet()) return@launch
            _isLoading.value = true
            try {
                // A. SUBIDA (Local -> Nube)
                val pendientes = productoDao.obtenerNoSincronizados()
                pendientes.forEach { prod ->
                    try {
                        if (prod.isDeleted) {
                            dynamoMapper.delete(prod)
                            productoDao.eliminar(prod)
                        } else {
                            dynamoMapper.save(prod)
                            prod.isSynced = true
                            productoDao.actualizar(prod)
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

                actualizarYNotificarTotal()
            } catch (e: Exception) {
                Log.e("SYNC", "Error general: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun actualizarYNotificarTotal() {
        val total = productoDao.contarProductos()
        lanzarNotificacion(total)
    }

    private fun lanzarNotificacion(cantidad: Int) {
        val context = getApplication<Application>()
        val channelId = "sync_channel_high_priority"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sincronización", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Inventario Actualizado")
            .setContentText("Total productos: $cantidad")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(1001, builder.build())
    }

    // ====================================================================================
    // 3. CRUD DE PRODUCTOS (CON VALIDACIÓN DE DISPONIBILIDAD)
    // ====================================================================================

    fun guardarProducto(producto: Producto) = viewModelScope.launch(Dispatchers.IO) {
        // Bloqueo de edición si disponibilidad es 0 (false)
        val existente = productoDao.obtenerPorCodigo(producto.codigo)
        if (existente != null && !existente.isDisponible) {
            MsgDropClient.obtenerMensaje { _errorMsgDrop.value = it }
            return@launch
        }

        producto.isSynced = false
        producto.isDeleted = false
        productoDao.insertar(producto)

        if (hayInternet()) {
            try {
                dynamoMapper.save(producto)
                producto.isSynced = true
                productoDao.actualizar(producto)
                actualizarYNotificarTotal()
            } catch (e: Exception) {
                Log.e("AWS", "Error subida: ${e.message}")
            }
        }
    }

    fun eliminarProducto(producto: Producto) = viewModelScope.launch(Dispatchers.IO) {
        // Bloqueo de eliminación si disponibilidad es 0 (false)
        if (!producto.isDisponible) {
            MsgDropClient.obtenerMensaje { _errorMsgDrop.value = it }
            return@launch
        }

        producto.isDeleted = true
        producto.isSynced = false
        productoDao.actualizar(producto)

        if (hayInternet()) {
            try {
                dynamoMapper.delete(producto)
                productoDao.eliminar(producto)
                actualizarYNotificarTotal()
            } catch (e: Exception) {
                Log.e("AWS", "Error eliminación: ${e.message}")
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
            val nuevo = Usuario(usuario = nombre, clave = cifrarPass(pass))
            usuarioDao.insertar(nuevo)
            if (hayInternet()) { try { dynamoMapper.save(nuevo) } catch (e: Exception){} }
            true
        }
    }

    suspend fun login(nombre: String, pass: String): Usuario? {
        return withContext(Dispatchers.IO) {
            usuarioDao.autenticar(nombre, cifrarPass(pass))
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
    // 5. VALIDACIONES EXTRAS
    // ====================================================================================

    fun esPasswordSegura(password: String): String? {
        if (password.length < 6) return "Mínimo 6 caracteres."
        if (!password.any { it.isDigit() }) return "Debe incluir un número."
        if (!password.any { it.isUpperCase() }) return "Debe incluir una mayúscula."
        return null
    }

    fun esUsuarioValido(usuario: String): String? {
        if (usuario.trim().length < 4) return "Mínimo 4 caracteres."
        if (usuario.contains(" ")) return "Sin espacios."
        return null
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