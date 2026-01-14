package com.example.inventory.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.User
import com.example.inventory.data.UsersRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun updateUiState(loginDetails: LoginDetails) {
        loginUiState = LoginUiState(loginDetails = loginDetails, isEntryValid = validateInput(loginDetails))
    }

    private fun validateInput(uiState: LoginDetails = loginUiState.loginDetails): Boolean {
        return with(uiState) {
            username.isNotBlank() && password.isNotBlank()
        }
    }

    suspend fun loginUser(): Boolean {
        return if (validateInput()) {
            val user = usersRepository.getUser(
                loginUiState.loginDetails.username,
                loginUiState.loginDetails.password
            )
            user != null
        } else {
            false
        }
    }

    fun signUp(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (validateInput()) {
                usersRepository.insertUser(loginUiState.loginDetails.toUser())
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }
}

data class LoginUiState(
    val loginDetails: LoginDetails = LoginDetails(),
    val isEntryValid: Boolean = false
)

data class LoginDetails(
    val username: String = "",
    val password: String = "",
)

fun LoginDetails.toUser(): User = User(
    username = username,
    password = password
)
