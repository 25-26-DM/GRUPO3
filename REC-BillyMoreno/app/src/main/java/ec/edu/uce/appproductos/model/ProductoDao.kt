package ec.edu.uce.appproductos.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    // --- CAMBIO CLAVE AQUÍ ---
    // Filtramos con "WHERE is_deleted = 0".
    // Así, los productos marcados para borrar desaparecen de la lista visualmente,
    // pero siguen en la base de datos hasta que el sincronizador los borre de la nube.
    @Query("SELECT * FROM productos WHERE is_deleted = 0")
    fun obtenerTodos(): Flow<List<Producto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    // Usamos actualizar tanto para editar datos como para el "Soft Delete" (marcar isDeleted=true)
    @Update
    suspend fun actualizar(producto: Producto)

    // Este es el borrado físico real (lo usaremos SOLO después de borrar en la nube)
    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("SELECT * FROM productos WHERE codigo = :codigo")
    suspend fun obtenerPorCodigo(codigo: String): Producto?

    // --- SINCRONIZACIÓN ---
    // Esta consulta trae TODO lo que falta procesar (Creados, Editados y Borrados Lógicos)
    // Funciona porque cuando marcas isDeleted=true, también marcarás isSynced=false.
    @Query("SELECT * FROM productos WHERE is_synced = 0")
    suspend fun obtenerNoSincronizados(): List<Producto>

    // --- NUEVO: CONTADOR ---
    @Query("SELECT COUNT(*) FROM productos WHERE is_deleted = 0")
    suspend fun contarProductos(): Int

    @Query("SELECT * FROM productos WHERE is_deleted = 0 AND descripcion LIKE '%' || :query || '%'")
    fun buscarProductos(query: String): Flow<List<Producto>>
}