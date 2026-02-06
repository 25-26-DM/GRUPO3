package ec.edu.uce.appproductos.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario = :nombre AND clave = :clave LIMIT 1")
    suspend fun autenticar(nombre: String, clave: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE usuario = :nombre LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): Usuario?

    // --- AGREGAR ESTO ---
    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<Usuario>
}