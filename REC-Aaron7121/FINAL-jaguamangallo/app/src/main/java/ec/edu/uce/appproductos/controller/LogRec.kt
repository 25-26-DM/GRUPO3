package ec.edu.uce.appproductos.controller

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import ec.edu.uce.appproductos.model.AppDatabase
import ec.edu.uce.appproductos.model.LogDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogRec(private val context: Context) {

    private val logDao: LogDao = AppDatabase.getDatabase(context).logDao()
    private val mapper: DynamoDBMapper = AwsConfig.getDynamoDBMapper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // Enumeración para las acciones
    enum class Accion {
        INGRESO,
        CREACION,
        ELIMINACION,
        ACTUALIZACION,
        SINCRONIZACION,
        ERROR
    }

    enum class Recurso {
        PRODUCTO,
        USUARIO,
        SISTEMA
    }

    /**
     * Registra una acción en la BD local (Room) y marca para sincronización
     */
    suspend fun registrarAccion(
        accion: Accion,
        usuario: String,
        recurso: Recurso,
        idRecurso: String,
        detalles: String = ""
    ) = withContext(Dispatchers.IO) {
        try {
            val ahora = dateFormat.format(Date())
            val log = ec.edu.uce.appproductos.model.Log(
                accion = accion.name,
                usuario = usuario,
                fecha = ahora,
                detalles = detalles,
                recurso = recurso.name,
                idRecurso = idRecurso,
                sincronizado = false
            )
            logDao.insertarLog(log)
            Log.d("LogRec", "Acción registrada: $accion por $usuario")
        } catch (e: Exception) {
            Log.e("LogRec", "Error al registrar acción: ${e.message}")
        }
    }

    /**
     * Obtiene los logs pendientes de sincronizar y los envía a DynamoDB
     */
    suspend fun sincronizarLogs() = withContext(Dispatchers.IO) {
        try {
            val logsPendientes = logDao.obtenerLogsPendientesDeSincronizar()

            if (logsPendientes.isEmpty()) {
                Log.d("LogRec", "No hay logs pendientes de sincronizar")
                return@withContext
            }

            for (log in logsPendientes) {
                try {
                    // Envía a DynamoDB en hilo de IO
                    kotlin.runCatching {
                        mapper.save(log)
                    }.onSuccess {
                        // Marca como sincronizado en Room
                        log.sincronizado = true
                        logDao.actualizarLog(log)
                        Log.d("LogRec", "Log sincronizado: ${log.logId}")
                    }.onFailure { e ->
                        Log.e("LogRec", "Error sincronizando log ${log.logId}: ${e.message}")
                    }
                } catch (e: Exception) {
                    Log.e("LogRec", "Error en bucle de sincronización: ${e.message}")
                }
            }

            Log.d("LogRec", "Sincronización completada: ${logsPendientes.size} logs")
        } catch (e: Exception) {
            Log.e("LogRec", "Error en sincronizarLogs: ${e.message}")
        }
    }

    /**
     * Obtiene todos los logs registrados
     */
    suspend fun obtenerTodosLosLogs(): List<ec.edu.uce.appproductos.model.Log> = withContext(Dispatchers.IO) {
        return@withContext try {
            logDao.obtenerTodosLosLogs()
        } catch (e: Exception) {
            Log.e("LogRec", "Error obteniendo logs: ${e.message}")
            emptyList()
        }
    }

    /**
     * Obtiene logs de un usuario específico
     */
    suspend fun obtenerLogsPorUsuario(usuario: String): List<ec.edu.uce.appproductos.model.Log> = withContext(Dispatchers.IO) {
        return@withContext try {
            logDao.obtenerLogsPorUsuario(usuario)
        } catch (e: Exception) {
            Log.e("LogRec", "Error obteniendo logs del usuario: ${e.message}")
            emptyList()
        }
    }

    /**
     * Obtiene logs por tipo de acción
     */
    suspend fun obtenerLogsPorAccion(accion: Accion): List<ec.edu.uce.appproductos.model.Log> = withContext(Dispatchers.IO) {
        return@withContext try {
            logDao.obtenerLogsPorAccion(accion.name)
        } catch (e: Exception) {
            Log.e("LogRec", "Error obteniendo logs por acción: ${e.message}")
            emptyList()
        }
    }

    /**
     * Elimina logs más antiguos a 30 días
     */
    suspend fun limpiarLogsAntiguos() = withContext(Dispatchers.IO) {
        try {
            logDao.eliminarLogsAntiguos()
            Log.d("LogRec", "Logs antiguos eliminados")
        } catch (e: Exception) {
            Log.e("LogRec", "Error al limpiar logs: ${e.message}")
        }
    }

    /**
     * Obtiene el conteo total de logs
     */
    suspend fun obtenerConteoLogs(): Int = withContext(Dispatchers.IO) {
        return@withContext try {
            logDao.contarTodosLosLogs()
        } catch (e: Exception) {
            Log.e("LogRec", "Error al contar logs: ${e.message}")
            0
        }
    }
}
