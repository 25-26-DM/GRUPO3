package ec.edu.uce.appproductos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import ec.edu.uce.appproductos.controller.AppNavigation // <--- IMPORTANTE: Importamos tu NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Creamos el controlador de navegación (el "cerebro")
            val navController = rememberNavController()

            // 2. Llamamos a tu función de navegación que está en la carpeta 'controller'
            // Esto carga el Login, Registro, Home, etc.
            AppNavigation(navController = navController)
        }
    }
}