package com.adrian.blok.core.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AuthUiState(
    val loading: Boolean = true,
    val user: UserDto? = null,
    val error: String? = null,
    val busy: Boolean = false,
)

class AuthViewModel(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val user = repository.restoreSession()
            _state.value = AuthUiState(loading = false, user = user)
        }
    }

    fun login(email: String, password: String) = authenticate(email, password, register = false)

    fun register(email: String, password: String) = authenticate(email, password, register = true)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _state.value = AuthUiState(loading = false, user = null)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun authenticate(email: String, password: String, register: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(busy = true, error = null)
            try {
                val user = if (register) {
                    repository.register(email.trim(), password)
                } else {
                    repository.login(email.trim(), password)
                }
                _state.value = AuthUiState(loading = false, user = user, busy = false)
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    busy = false,
                    error = e.message() ?: "Error de autenticación (${e.code()})",
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    busy = false,
                    error = e.message ?: "No se pudo conectar con la API",
                )
            }
        }
    }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(repository) as T
                }
            }
    }
}
