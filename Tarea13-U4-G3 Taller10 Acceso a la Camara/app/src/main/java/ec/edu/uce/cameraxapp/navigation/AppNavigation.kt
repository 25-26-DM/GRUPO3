package ec.edu.uce.cameraxapp.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ec.edu.uce.cameraxapp.CameraScreen
import ec.edu.uce.cameraxapp.MyApplication
import ec.edu.uce.cameraxapp.ui.auth.AuthEvent
import ec.edu.uce.cameraxapp.ui.auth.AuthState
import ec.edu.uce.cameraxapp.ui.auth.AuthViewModel
import ec.edu.uce.cameraxapp.ui.auth.AuthViewModelFactory
import ec.edu.uce.cameraxapp.ui.auth.LoginScreen
import ec.edu.uce.cameraxapp.ui.auth.RegisterScreen
import ec.edu.uce.cameraxapp.ui.gallery.DetailScreen
import ec.edu.uce.cameraxapp.ui.gallery.GalleryScreen
import kotlinx.coroutines.flow.collectLatest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val application = context.applicationContext as MyApplication
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(application, application.repository)
    )
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        authViewModel.authEvent.collectLatest { event ->
            if (event is AuthEvent.SessionRestored) {
                Toast.makeText(context, "Sesión iniciada con ${event.username}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "loading"
    ) {
        composable("loading") { LoadingScreen() }
        composable("login") { LoginScreen(navController = navController, authViewModel = authViewModel) }
        composable("register") { RegisterScreen(navController = navController, authViewModel = authViewModel) }
        composable("camera") { CameraScreen(authViewModel = authViewModel, navController = navController) }
        composable("gallery") { GalleryScreen(navController = navController) }
        composable(
            route = "detail/{uri}/{isVideo}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("isVideo") { type = NavType.BoolType }
            )
        ) {
            val uri = it.arguments?.getString("uri") ?: ""
            val isVideo = it.arguments?.getBoolean("isVideo") ?: false
            DetailScreen(navController = navController, uri = uri, isVideo = isVideo)
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("camera") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.LoggedOut -> {
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Loading -> {
                // No hacemos nada, ya estamos en la pantalla de carga
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
