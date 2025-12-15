package ec.edu.uce.appproductos



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ec.edu.uce.appproductos.controller.Rutas
import ec.edu.uce.appproductos.model.Producto
import ec.edu.uce.appproductos.view.LoginScreen
import ec.edu.uce.appproductos.view.RegistroScreen
import ec.edu.uce.appproductos.view.HomeScreen
import ec.edu.uce.appproductos.view.ProductoFormScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // El NavController gestiona en qué pantalla estamos
            val navController = rememberNavController()

            // El NavHost es el contenedor que cambia las pantallas
            NavHost(navController = navController, startDestination = Rutas.LOGIN) {

                // Definimos la ruta "login"
                composable(Rutas.LOGIN) {
                    LoginScreen(navController)
                }

                // Definimos la ruta "registro" (Aún no creada, pero dejamos el hueco)
                composable(Rutas.REGISTRO) {
                    RegistroScreen(navController)
                }

                // Definimos la ruta "home"
                composable(Rutas.HOME) {
                    HomeScreen(navController)
                }

                composable (
                    route = "formulario_producto/{idProducto}",
                    arguments = listOf(navArgument("idProducto"){type = NavType.StringType})
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("idProducto")
                    ProductoFormScreen(navController, id)
                }
            }
        }
    }
}