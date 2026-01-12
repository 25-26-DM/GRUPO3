/*
 * Copyright (C) 2023 The Android Open Source Project
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package ec.edu.uce.marsphotos.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uce.marsphotos.R
import ec.edu.uce.marsphotos.ui.screens.HomeScreen
import ec.edu.uce.marsphotos.ui.screens.MarsViewModel
// Asegúrate de que este import sea correcto según el nombre que le puso tu compañero
import ec.edu.uce.marsphotos.ui.screens.UserInfoBar

@Composable
fun MarsPhotosApp(
    usuario: String, // Recibimos el usuario desde MainActivity
    hora: String     // Recibimos la hora desde MainActivity
) {
    // Configuramos el comportamiento del Scroll para que la barra se oculte al bajar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MarsTopAppBar(scrollBehavior = scrollBehavior) }
    ) { paddingValues ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val marsViewModel: MarsViewModel = viewModel()

            // ESTRUCTURA VERTICAL: HEADER + PANTALLA
            Column {

                // 1. HEADER (Rol A): Mostramos los datos que recibimos
                // Nota: Verifiqué en tus imports que se llama UserInfoBar, no UserHeader.
                UserInfoBar(
                    userName = usuario,
                    loginTime = hora,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 2. PANTALLA PRINCIPAL (Rol B): Fotos y consumo de datos
                HomeScreen(
                    marsUiState = marsViewModel.marsUiState,
                    modifier = Modifier.weight(1f) // Ocupa el resto del espacio
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