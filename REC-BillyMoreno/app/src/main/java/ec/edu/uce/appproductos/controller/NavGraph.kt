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
import ec.edu.uce.appproductos.view.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ProductoViewModel
) {
    NavHost(navController = navController, startDestination = Rutas.SPLASH) {

        // 1. RUTA SPLASH
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

        // 5. RUTA FORMULARIO (Crear o Editar producto) - CORREGIDA
        composable(
            route = "formulario_producto/{idProducto}/{emailUsuario}",
            arguments = listOf(
                navArgument("idProducto") { type = NavType.StringType },
                navArgument("emailUsuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("idProducto")
            val emailUsuario = backStackEntry.arguments?.getString("emailUsuario") ?: ""

            // ORDEN CORRECTO: (navController, idProducto, emailUsuario, viewModel)
            ProductoFormScreen(
                navController = navController,
                idProducto = id,
                emailUsuario = emailUsuario,
                viewModel = viewModel
            )
        }
    }
}