package com.example.misterdil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.data.remote.AdminProfile
import com.example.misterdil.data.repository.DossierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class CreateDossierState {
    object Idle : CreateDossierState()
    object Loading : CreateDossierState()
    object Success : CreateDossierState()
    data class Error(val message: String) : CreateDossierState()
}

class DossierViewModel(private val repository: DossierRepository) : ViewModel() {

    val dossiers: StateFlow<List<Dossier>> = repository.allDossiers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _createState = MutableStateFlow<CreateDossierState>(CreateDossierState.Idle)
    val createState: StateFlow<CreateDossierState> = _createState

    private val _admins = MutableStateFlow<List<AdminProfile>>(emptyList())
    val admins: StateFlow<List<AdminProfile>> = _admins

    private val _selectedAdminId = MutableStateFlow<Int?>(null)
    val selectedAdminId: StateFlow<Int?> = _selectedAdminId

    init {
        refresh()
        loadAdmins()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshDossiers()
        }
    }

    fun loadAdmins() {
        viewModelScope.launch {
            try {
                _admins.value = repository.getAdmins()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectAdmin(id: Int) {
        _selectedAdminId.value = id
    }

    fun createDossier(type: String, formData: Map<String, String>) {
        viewModelScope.launch {
            _createState.value = CreateDossierState.Loading
            try {
                repository.createDossier(type, formData)
                _createState.value = CreateDossierState.Success
            } catch (e: Exception) {
                _createState.value = CreateDossierState.Error(e.message ?: "Erreur lors de la création")
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateDossierState.Idle
    }
}

class DossierViewModelFactory(private val repository: DossierRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DossierViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DossierViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
