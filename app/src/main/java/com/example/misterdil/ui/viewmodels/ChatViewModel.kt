package com.example.misterdil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import com.example.misterdil.data.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ConversationCreateState {
    object Idle : ConversationCreateState()
    object Loading : ConversationCreateState()
    data class Success(val conversationId: String) : ConversationCreateState()
    data class Error(val message: String) : ConversationCreateState()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(
    private val repository: ChatRepository,
    val dossierRepository: DossierRepository
) : ViewModel() {

    private val _convCreateState = MutableStateFlow<ConversationCreateState>(ConversationCreateState.Idle)
    val convCreateState: StateFlow<ConversationCreateState> = _convCreateState

    private val _currentConversationId = MutableStateFlow<String?>(null)
    
    val conversations: StateFlow<List<Conversation>> = repository.allConversations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val messages: StateFlow<List<Message>> = _currentConversationId
        .flatMapLatest { id ->
            if (id == null) MutableStateFlow(emptyList())
            else repository.getMessages(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshConversations()
    }

    fun refreshConversations() {
        viewModelScope.launch {
            repository.refreshConversations()
        }
    }

    fun setConversation(id: String) {
        _currentConversationId.value = id
        refreshMessages()
    }

    fun refreshMessages() {
        val id = _currentConversationId.value ?: return
        viewModelScope.launch {
            repository.refreshMessages(id)
        }
    }

    fun sendMessage(text: String) {
        val id = _currentConversationId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(id, text)
        }
    }

    fun createConversationForDossier(adminId: String, adminName: String, dossierType: String, dossierId: String? = null) {
        viewModelScope.launch {
            _convCreateState.value = ConversationCreateState.Loading
            try {
                val convId = repository.createConversationForDossier(adminId, adminName, dossierType, dossierId)
                _convCreateState.value = ConversationCreateState.Success(convId)
                refreshConversations()
            } catch (e: Exception) {
                _convCreateState.value = ConversationCreateState.Error(e.message ?: "Erreur")
            }
        }
    }

    fun resetConvCreateState() {
        _convCreateState.value = ConversationCreateState.Idle
    }
}

class ChatViewModelFactory(
    private val repository: ChatRepository,
    private val dossierRepository: DossierRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository, dossierRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
