package com.example.misterdil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misterdil.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Authenticated : AuthUiState()
    object Unauthenticated : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val isAuthenticated: StateFlow<Boolean> = repository.authToken
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isAdmin: StateFlow<Boolean> = repository.userRole
        .map { it == "admin" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userName: StateFlow<String?> = repository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userEmail: StateFlow<String?> = repository.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val photoUri: StateFlow<String?> = repository.photoUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.login(email, password)
                _uiState.value = AuthUiState.Authenticated
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Échec de la connexion")
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String = "user") {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.register(name, email, password, role)
                _uiState.value = AuthUiState.Authenticated
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Échec de l'inscription")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.loginWithGoogle(idToken)
                _uiState.value = AuthUiState.Authenticated
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Échec Google Sign-In")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    fun updatePhotoUri(uri: String) {
        viewModelScope.launch { repository.savePhotoUri(uri) }
    }

    fun updateProfile(name: String, phone: String, language: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.updateProfile(name, phone, language)
                _uiState.value = AuthUiState.Authenticated
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Échec de la mise à jour")
                onError(e.message ?: "Échec de la mise à jour")
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.changePassword(currentPassword, newPassword)
                _uiState.value = AuthUiState.Authenticated
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Échec du changement de mot de passe")
                onError(e.message ?: "Échec du changement de mot de passe")
            }
        }
    }

    fun updateNotifications(notificationsEnabled: Boolean, paymentNotificationsEnabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateNotifications(notificationsEnabled, paymentNotificationsEnabled)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Échec de la mise à jour des notifications")
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.deleteAccount()
                _uiState.value = AuthUiState.Unauthenticated
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Échec de la suppression du compte")
                onError(e.message ?: "Échec de la suppression du compte")
            }
        }
    }

    fun resetError() { _uiState.value = AuthUiState.Idle }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
