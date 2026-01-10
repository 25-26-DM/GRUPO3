// src/main/java/.../LoginActivity.kt
package ec.edu.uce.actionopendocument

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uce.actionopendocument.databinding.ActivityLoginBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    // 1. Añadir una instancia del DatabaseHelper
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Inicializar el DatabaseHelper
        dbHelper = DatabaseHelper(this)

        binding.buttonLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = binding.editTextUsername.text.toString().trim() // .trim() para quitar espacios
        val password = binding.editTextPassword.text.toString()

        // 3. Modificar la validación para que use la base de datos
        if (username.isNotEmpty() && password.isNotEmpty()) {
            if (dbHelper.validateUser(username, password)) {
                // Si las credenciales son válidas:
                // Guardar datos de ingreso en SharedPreferences
                saveLoginInfo(username)

                // Navegar a la pantalla principal
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Cierra la pantalla de login
            } else {
                // Si las credenciales son incorrectas
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Si los campos están vacíos
            Toast.makeText(this, "Por favor, ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLoginInfo(username: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss a", Locale.getDefault())
        val currentDate = sdf.format(Date())

        // TRUCO: Creamos una llave única usando el tiempo en milisegundos
        val uniqueKey = "Ingreso_" + System.currentTimeMillis()

        // Guardamos todo en una sola línea para que se vea ordenado en el XML
        val valorAGuardar = "Usuario: $username | Fecha: $currentDate"

        editor.putString(uniqueKey, valorAGuardar)

        editor.apply()
    }
}
