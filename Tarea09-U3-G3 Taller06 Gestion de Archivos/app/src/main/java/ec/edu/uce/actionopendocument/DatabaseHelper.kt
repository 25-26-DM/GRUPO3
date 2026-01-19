package ec.edu.uce.actionopendocument

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Constantes para la base de datos y la tabla
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val TABLE_USERS = "users"
        private const val KEY_ID = "id"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_PASSWORD = "password"
    }

    // Este método se llama solo la primera vez que se crea la base de datos
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_USERS("
                + "$KEY_ID INTEGER PRIMARY KEY,"
                + "$KEY_USER_NAME TEXT,"
                + "$KEY_PASSWORD TEXT" + ")")
        db?.execSQL(createTableQuery)

        // Pre-poblar la base de datos con los usuarios del grupo
        populateInitialUsers(db)
    }

    // Este método se llama si actualizas DATABASE_VERSION en el futuro
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * Inserta los 6 usuarios iniciales en la base de datos.
     */
    private fun populateInitialUsers(db: SQLiteDatabase?) {
        val users = listOf(
            "Alexis", "David", "Damian", "Jostyn", "Billy", "Joel"
        )
        val passwordForAll = "grupo3"

        db?.let {
            for (user in users) {
                val values = ContentValues().apply {
                    put(KEY_USER_NAME, user)
                    put(KEY_PASSWORD, passwordForAll)
                }
                it.insert(TABLE_USERS, null, values)
            }
        }
    }

    /**
     * Verifica si un usuario y contraseña coinciden con los registros de la BD.
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    fun validateUser(userName: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS, // La tabla para consultar
            arrayOf(KEY_ID), // Las columnas a devolver (solo necesitamos saber si existe)
            "$KEY_USER_NAME = ? AND $KEY_PASSWORD = ?", // La cláusula WHERE
            arrayOf(userName, password), // Los valores para la cláusula WHERE
            null, null, null
        )

        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }
}
