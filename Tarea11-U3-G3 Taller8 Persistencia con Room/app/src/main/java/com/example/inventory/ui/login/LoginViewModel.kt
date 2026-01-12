package com.example.inventory.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.User
import com.example.inventory.data.UsersRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun updateUiState(userDetails: UserDetails) {
        loginUiState = LoginUiState(userDetails = userDetails, isInputValid = validateInput(userDetails))
    }

    private fun validateInput(uiState: UserDetails = loginUiState.userDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && password.isNotBlank()
        }
    }

    suspend fun loginUser(): Boolean {
        if (validateInput()) {
            val user = usersRepository.getUserStream(
                loginUiState.userDetails.name,
                loginUiState.userDetails.password
            ).first()
            return user != null
        }
        return false
    }

    suspend fun registerUser() {
        if (validateInput()) {
            usersRepository.insertUser(loginUiState.userDetails.toUser())
        }
    }
}

data class LoginUiState(
    val userDetails: UserDetails = UserDetails(),
    val isInputValid: Boolean = false
)

data class UserDetails(
    val id: Int = 0,
    val name: String = "",
    val password: String = ""
)

fun UserDetails.toUser(): User = User(
    id = id,
    name = name,
    password = password
)
