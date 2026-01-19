package com.example.android.whileinuselocation

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
        
        // Lista de usuarios a agregar
        val users = listOf(
            "admin" to "1234",
            "Damian" to "claveDamian",
            "Alexis" to "claveAlexis",
            "David" to "claveDavid",
            "Joel" to "claveJoel",
            "Billy" to "claveBilly"
        )

        for ((username, password) in users) {
            val values = ContentValues().apply {
                put(COLUMN_USERNAME, username)
                put(COLUMN_PASSWORD, hashPassword(password))
            }
            db.insert(TABLE_USERS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * Genera un hash SHA-256 de la contraseña para no guardarla en texto plano.
     */
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun validateUser(username: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, hashedPassword)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    companion object {
        private const val DATABASE_VERSION = 2 // Incrementado para aplicar cambios
        private const val DATABASE_NAME = "UserManager.db"
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        private const val CREATE_TABLE_USERS = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")")
    }
}
