package ec.edu.uce.appproductos.controller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ec.edu.uce.appproductos.view.HomeScreen
import ec.edu.uce.appproductos.view.LoginScreen
import ec.edu.uce.appproductos.view.ProductoFormScreen
import ec.edu.uce.appproductos.view.RegistroScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

        composable(Rutas.LOGIN) { LoginScreen(navController) }
        composable(Rutas.REGISTRO) { RegistroScreen(navController) }

        // CAMBIO REALIZADO: Ahora la ruta recibe 'nombre', 'pass' y 'hash'
        composable(
            route = "home/{nombre}/{pass}/{hash}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType }, // <--- Nuevo argumento
                navArgument("pass") { type = NavType.StringType },
                navArgument("hash") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Recuperamos los 3 datos de los argumentos
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val pass = backStackEntry.arguments?.getString("pass") ?: ""
            val hash = backStackEntry.arguments?.getString("hash") ?: ""

            // Se los pasamos a la pantalla HomeScreen
            HomeScreen(navController, nombre, pass, hash)
        }

        composable(
            route = "formulario_producto/{idProducto}",
            arguments = listOf(navArgument("idProducto") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("idProducto")
            ProductoFormScreen(navController, id)
        }
    }
}