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
import ec.edu.uce.appproductos.view.SplashScreen // <--- Asegúrate de importar esto

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ProductoViewModel
) {
    // CAMBIO CLAVE: startDestination ahora es Rutas.SPLASH para que inicie con la animación
    NavHost(navController = navController, startDestination = Rutas.SPLASH) {

        // 1. RUTA SPLASH (NUEVA - Pantalla de carga animada)
        composable(Rutas.SPLASH) {
            SplashScreen(navController)
        }

        // 2. RUTA LOGIN
        composable(Rutas.LOGIN) {
            LoginScreen(navController, viewModel)
        }

        // 3. RUTA REGISTRO
        composable(Rutas.REGISTRO) {
            RegistroScreen(navController, viewModel)
        }

        // 4. RUTA HOME (Con argumentos de usuario)
        composable(
            route = "home/{nombre}/{pass}/{hash}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("pass") { type = NavType.StringType },
                navArgument("hash") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val pass = backStackEntry.arguments?.getString("pass") ?: ""
            val hash = backStackEntry.arguments?.getString("hash") ?: ""

            HomeScreen(navController, nombre, pass, hash, viewModel)
        }

        // 5. RUTA FORMULARIO (Crear o Editar producto)
        composable(
            route = "formulario_producto/{idProducto}",
            arguments = listOf(navArgument("idProducto") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("idProducto")
            ProductoFormScreen(navController, id, viewModel)
        }
    }
}