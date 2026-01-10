/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package ec.edu.uce.marsphotos.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uce.marsphotos.R
import ec.edu.uce.marsphotos.ui.screens.HomeScreen
import ec.edu.uce.marsphotos.ui.screens.LoginScreen
import ec.edu.uce.marsphotos.ui.screens.MarsViewModel
import ec.edu.uce.marsphotos.ui.screens.UserInfoBar

@OptIn(ExperimentalMaterial3Api::class) // Necesario para usar TopAppBarDefaults
@Composable
fun MarsPhotosApp() {
    // 1. Configuramos el comportamiento del Scroll (ESTO FALTABA)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Esto conecta con la lógica de negocio para bajar las fotos
    val marsViewModel: MarsViewModel = viewModel()

    // Variables para guardar el estado de si estamos logueados o no
    var isLoggedIn by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var loginTime by remember { mutableStateOf("") }

    Scaffold(
        topBar = { MarsTopAppBar(scrollBehavior = scrollBehavior) } // Tu barra superior existente
    ) { paddingValues ->

        Surface(modifier = Modifier.padding(paddingValues)) {
            if (isLoggedIn) {
                // AQUÍ VA LA PANTALLA DEL ROL B (HomeScreen)
                // Le pasamos los datos para que los muestre en el Header
                Column {
                    // --- AQUÍ USAMOS TU NUEVO COMPONENTE ---
                    UserInfoBar(
                        userName = userName,
                        loginTime = loginTime
                    )

                    // La pantalla del Rol B va justo debajo
                    HomeScreen(
                        marsUiState = marsViewModel.marsUiState,
                        modifier = Modifier.padding(top = 0.dp)
                    )
                }
            } else {
                // MUESTRA EL LOGIN (ROL A)
                LoginScreen(
                    onLoginSuccess = { nombre, hora ->
                        userName = nombre
                        loginTime = hora
                        isLoggedIn = true // Esto cambia la pantalla automáticamente
                    }
                )
            }
        }
    }
}


@Composable
fun MarsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}
