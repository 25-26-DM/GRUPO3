package ec.edu.uce.appproductos.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// IMPORTANTE:
// 1. Entities: Debe tener [Producto::class, Usuario::class]
// 2. Version: 3 (Para soportar los cambios de Soft Delete)
@Database(entities = [Producto::class, Usuario::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao

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