package ec.edu.uce.appproductos.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// IMPORTANTE:
// 1. Entities: Debe tener [Producto::class, Usuario::class, Log::class]
// 2. Version: 4 (Agregamos tabla de logs)
@Database(entities = [Producto::class, Usuario::class, Log::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun logDao(): LogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_productos_db"
                )
                    // Esta línea evita que la app se cierre si cambias la versión de la BD
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}