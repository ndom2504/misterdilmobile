package com.example.misterdil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.data.repository.DossierRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DossierViewModel(private val repository: DossierRepository) : ViewModel() {

    val dossiers: StateFlow<List<Dossier>> = repository.allDossiers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshDossiers()
        }
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
