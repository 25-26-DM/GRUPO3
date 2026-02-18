package ec.edu.uce.appproductos.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LogDao {

    @Insert
    suspend fun insertarLog(log: Log)

    @Update
    suspend fun actualizarLog(log: Log)

    @Delete
    suspend fun eliminarLog(log: Log)

    @Query("SELECT * FROM logs WHERE logId = :logId")
    suspend fun obtenerLogPorId(logId: String): Log?

    @Query("SELECT * FROM logs ORDER BY fecha DESC")
    suspend fun obtenerTodosLosLogs(): List<Log>

    @Query("SELECT * FROM logs WHERE usuario = :usuario ORDER BY fecha DESC")
    suspend fun obtenerLogsPorUsuario(usuario: String): List<Log>

    @Query("SELECT * FROM logs WHERE accion = :accion ORDER BY fecha DESC")
    suspend fun obtenerLogsPorAccion(accion: String): List<Log>

    @Query("SELECT * FROM logs WHERE sincronizado = 0")
    suspend fun obtenerLogsPendientesDeSincronizar(): List<Log>

    @Query("DELETE FROM logs WHERE fecha < datetime('now', '-30 days')")
    suspend fun eliminarLogsAntiguos()

    @Query("SELECT COUNT(*) FROM logs")
    suspend fun contarTodosLosLogs(): Int
}
