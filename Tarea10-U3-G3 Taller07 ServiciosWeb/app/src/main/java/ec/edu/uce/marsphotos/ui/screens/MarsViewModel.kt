/*
 * Copyright (C) 2023 The Android Open Source Project
 */
package ec.edu.uce.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.uce.marsphotos.network.MarsApi
import ec.edu.uce.marsphotos.network.MarsPhoto
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// --- PARTE 1: DEFINIMOS LOS ESTADOS (Soluciona los errores rojos de HomeScreen) ---
/**
 * UI state for the Home screen
 */
sealed interface MarsUiState {
    data class Success(val photos: List<MarsPhoto>) : MarsUiState // Éxito: Tenemos lista de fotos
    object Error : MarsUiState                                    // Error: Algo falló
    object Loading : MarsUiState                                  // Cargando: Esperando respuesta
}

class MarsViewModel : ViewModel() {

    // --- PARTE 2: VARIABLE DE ESTADO ---
    // Ya no es un String, ahora es del tipo MarsUiState que definimos arriba
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    init {
        getMarsPhotos()
    }

    // --- PARTE 3: CONEXIÓN REAL A INTERNET (Lógica Rol B) ---
    fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = MarsUiState.Loading // Ponemos estado "Cargando"
            marsUiState = try {
                // LLAMADA A LA RED: Usamos el objeto MarsApi (Retrofit)
                // Esto baja el JSON y lo convierte en lista de fotos
                val listResult = MarsApi.retrofitService.getPhotos()

                // Si funciona, guardamos la lista en el estado Success
                MarsUiState.Success(listResult)
            } catch (e: IOException) {
                MarsUiState.Error // Error de conexión (sin internet)
            } catch (e: HttpException) {
                MarsUiState.Error // Error del servidor (404, 500)
            }
        }
    }
}