package com.example.misterdil.data.repository

import com.example.misterdil.data.local.ConversationDao
import com.example.misterdil.data.local.MessageDao
import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import com.example.misterdil.data.remote.ChatApiService
import com.example.misterdil.data.remote.CreateConversationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(
    private val apiService: ChatApiService,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao
) {
    val allConversations: Flow<List<Conversation>> = conversationDao.getAllConversations()

    fun getMessages(conversationId: String): Flow<List<Message>> = 
        messageDao.getMessagesForConversation(conversationId)

    suspend fun refreshConversations() {
        withContext(Dispatchers.IO) {
            try {
                val remoteConversations = apiService.getConversations()
                conversationDao.insertConversations(remoteConversations)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun refreshMessages(conversationId: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteMessages = apiService.getMessages(conversationId)
                messageDao.insertMessages(remoteMessages)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun sendMessage(conversationId: String, text: String) {
        withContext(Dispatchers.IO) {
            try {
                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    conversationId = conversationId,
                    senderId = "me",
                    text = text,
                    timestamp = System.currentTimeMillis(),
                    isFromMe = true
                )
                messageDao.insertMessage(newMessage)
                apiService.sendMessage(conversationId, newMessage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun createConversationForDossier(
        adminId: String,
        adminName: String,
        dossierType: String,
        dossierId: String? = null
    ): String {
        return withContext(Dispatchers.IO) {
            val conversation = apiService.createConversation(
                CreateConversationRequest(adminId, adminName, dossierType, dossierId)
            )
            conversationDao.insertConversations(listOf(conversation))
            // Message auto-généré supprimé - le client envoie maintenant un message de récapitulatif personnalisé
            conversation.id
        }
    }
}
