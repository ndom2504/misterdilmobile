package com.example.misterdil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misterdil.data.remote.PaymentApiService
import com.example.misterdil.data.remote.PaymentIntentRequest
import com.example.misterdil.data.remote.PaymentIntentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Loading : PaymentUiState()
    data class Success(val response: PaymentIntentResponse) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class PaymentViewModel(private val apiService: PaymentApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState

    fun preparePayment(amount: Long, currency: String = "cad") {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            try {
                val response = apiService.createPaymentIntent(PaymentIntentRequest(amount, currency))
                _uiState.value = PaymentUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.message ?: "Erreur lors de la préparation du paiement")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = PaymentUiState.Idle
    }
}

class PaymentViewModelFactory(private val apiService: PaymentApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
