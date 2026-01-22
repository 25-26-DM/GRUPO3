package ec.edu.uce.cameraxapp.ui.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ec.edu.uce.cameraxapp.data.local.User
import ec.edu.uce.cameraxapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class AuthViewModel(application: Application, private val userRepository: UserRepository) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkActiveSession()
    }

    private fun checkActiveSession() {
        viewModelScope.launch {
            val username = sharedPreferences.getString("logged_in_username", null)
            if (username != null) {
                val user = userRepository.getUserByUsername(username)
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                    _authEvent.emit(AuthEvent.SessionRestored(user.username))
                } else {
                    _authState.value = AuthState.LoggedOut
                }
            } else {
                _authState.value = AuthState.LoggedOut
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            if (userRepository.getUserByUsername(username) != null) {
                _authEvent.emit(AuthEvent.Error("El nombre de usuario ya existe"))
                return@launch
            }
            if (userRepository.getUserByEmail(email) != null) {
                _authEvent.emit(AuthEvent.Error("El email ya está registrado"))
                return@launch
            }

            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val user = User(username = username, email = email, passwordHash = hashedPassword)
            userRepository.insert(user)
            _currentUser.value = user
            _authState.value = AuthState.Authenticated
            sharedPreferences.edit().putString("logged_in_username", username).apply()
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByUsername(username)
            if (user != null && BCrypt.checkpw(password, user.passwordHash)) {
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
                sharedPreferences.edit().putString("logged_in_username", username).apply()
            } else {
                _authEvent.emit(AuthEvent.Error("Nombre de usuario o contraseña incorrectos"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sharedPreferences.edit().clear().apply()
            _currentUser.value = null
            _authState.value = AuthState.LoggedOut
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object LoggedOut : AuthState()
}

sealed class AuthEvent {
    data class Error(val message: String) : AuthEvent()
    data class SessionRestored(val username: String) : AuthEvent()
}


class AuthViewModelFactory(private val application: Application, private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(application, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
