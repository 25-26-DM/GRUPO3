package ec.edu.uce.actionopendocument


import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.commit

// Asegúrate de que este ID sea correcto en tu layout xml
const val DOCUMENT_FRAGMENT_TAG = "ec.edu.uce.actionopendocument.tags.DOCUMENT_FRAGMENT"

private const val TAG = "MainActivity"
private const val LAST_OPENED_URI_KEY =
    "ec.edu.uce.actionopendocument.pref.LAST_OPENED_URI_KEY"

class MainActivity : AppCompatActivity() {

    private lateinit var noDocumentView: ViewGroup

    private val openDocumentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.also { documentUri ->
                // Permisos para mantener acceso al archivo a largo plazo
                contentResolver.takePersistableUriPermission(
                    documentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                openDocument(documentUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate de que tu archivo XML se llame realmente 'activity_main_real.xml'
        setContentView(R.layout.activity_main_real)

        // Referencias a las vistas usando R de tu propio paquete (ahora funcionará)
        noDocumentView = findViewById(R.id.no_document_view)

        findViewById<Button>(R.id.open_file).setOnClickListener {
            openDocumentPicker()
        }

        getSharedPreferences(TAG, MODE_PRIVATE).let { sharedPreferences ->
            if (sharedPreferences.contains(LAST_OPENED_URI_KEY)) {
                val documentUri =
                    sharedPreferences.getString(LAST_OPENED_URI_KEY, null)?.toUri() ?: return@let
                openDocument(documentUri)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Asegúrate de tener res/menu/main.xml creado
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                // He corregido el AlertDialog para usar recursos de String correctamente
                AlertDialog.Builder(this)
                    .setMessage(R.string.intro_message)
                    // .setPositiveButton(android.R.string.ok, null) // Si quieres usar el "OK" del sistema
                    .setPositiveButton("OK", null) // O texto directo si no tienes el recurso
                    .show()
                return true
            }
            R.id.action_open -> {
                openDocumentPicker()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf" // O "*/*" para todos los archivos
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        openDocumentLauncher.launch(intent)
    }

    private fun openDocument(documentUri: Uri) {
        getSharedPreferences(TAG, MODE_PRIVATE).edit {
            putString(LAST_OPENED_URI_KEY, documentUri.toString())
        }

        // Asegúrate de que ActionOpenDocumentFragment exista y tenga el método newInstance
        val fragment = ActionOpenDocumentFragment.newInstance(documentUri)

        supportFragmentManager.commit {
            // Asegúrate de que en tu XML haya un contenedor (FrameLayout o similar) con id 'container'
            add(R.id.container, fragment, DOCUMENT_FRAGMENT_TAG)
        }

        noDocumentView.visibility = View.GONE
    }
}