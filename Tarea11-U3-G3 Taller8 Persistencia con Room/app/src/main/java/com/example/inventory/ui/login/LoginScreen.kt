package com.example.inventory.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Inicio de Sesión") })
        }
    ) { innerPadding ->
        LoginBody(
            loginUiState = viewModel.loginUiState,
            onValueChange = viewModel::updateUiState,
            onLoginClick = {
                coroutineScope.launch {
                    if (viewModel.loginUser()) {
                        navigateToHome()
                    }
                }
            },
            onRegisterClick = {
                coroutineScope.launch {
                    viewModel.registerUser()
                }
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun LoginBody(
    loginUiState: LoginUiState,
    onValueChange: (UserDetails) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen PNG: Cambia 'ic_launcher_foreground' por el nombre de tu archivo PNG
        Image(
            painter = painterResource(id = R.drawable.imagen_logo), // Ejemplo temporal
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = loginUiState.userDetails.name,
            onValueChange = { onValueChange(loginUiState.userDetails.copy(name = it)) },
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = loginUiState.userDetails.password,
            onValueChange = { onValueChange(loginUiState.userDetails.copy(password = it)) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLoginClick,
            enabled = loginUiState.isInputValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRegisterClick,
            enabled = loginUiState.isInputValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Cuenta")
        }
    }
}
