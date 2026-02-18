package ec.edu.uce.appproductos.controller

object SessionManager {
    // Tiempos en milisegundos
    private const val TIEMPO_MAXIMO_SESION = 15 * 60 * 1000L // 15 minutos

    private const val TIEMPO_EXPIRACION_INACTIVIDAD = 5 * 60 * 1000L // 5 minutos


    var tiempoInicioSesion: Long = 0
    var tiempoUltimaActividad: Long = 0
    var isUsuarioLogueado: Boolean = false
    var usuarioActual: String = "SISTEMA" // Usuario actual logueado

    // Se llama cuando el usuario hace Login exitoso
    fun iniciarSesion(usuario: String = "") {
        val ahora = System.currentTimeMillis()
        tiempoInicioSesion = ahora
        tiempoUltimaActividad = ahora
        isUsuarioLogueado = true
        usuarioActual = usuario
    }

    // Obtener usuario actual
    fun obtenerUsuarioActual(): String {
        return if (isUsuarioLogueado) usuarioActual else "SISTEMA"
    }

    // Se llama cada vez que el usuario toca la pantalla
    fun registrarActividad() {
        if (isUsuarioLogueado) {
            tiempoUltimaActividad = System.currentTimeMillis()
        }
    }

    // Se llama para cerrar sesión manualmente o por tiempo
    fun cerrarSesion() {
        isUsuarioLogueado = false
        tiempoInicioSesion = 0
        tiempoUltimaActividad = 0
        usuarioActual = "SISTEMA"
    }

    // Verifica si la sesión sigue válida. Retorna un mensaje de error o null si todo está bien.
    fun verificarEstadoSesion(): String? {
        if (!isUsuarioLogueado) return null

        val ahora = System.currentTimeMillis()

        // 1. Verificar Duración Máxima (15 min)
        if (ahora - tiempoInicioSesion > TIEMPO_MAXIMO_SESION) {
            return "Sesión finalizada por tiempo máximo (15 min)"
        }

        // 2. Verificar Inactividad (5 min)
        if (ahora - tiempoUltimaActividad > TIEMPO_EXPIRACION_INACTIVIDAD) {
            return "Sesión cerrada por inactividad (5 min)"
        }

        return null // La sesión es válida
    }
}