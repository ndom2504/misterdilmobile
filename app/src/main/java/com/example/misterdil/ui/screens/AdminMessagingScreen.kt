package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMessagingScreen(
    viewModel: ChatViewModel,
    onNavigateToDossier: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    val conversations by viewModel.conversations.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet) {
        // Tablet split view
        Row(modifier = modifier.fillMaxSize()) {
            // Inbox (gauche)
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AdminInboxScreen(
                    conversations = conversations,
                    onConversationClick = { 
                        selectedConversation = it
                        viewModel.setConversation(it.id)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            @OptIn(ExperimentalMaterial3Api::class)
            VerticalDivider()
            // Conversation détail (droite)
            if (selectedConversation != null) {
                AdminConversationDetailScreen(
                    conversation = selectedConversation!!,
                    onBack = { selectedConversation = null },
                    onNavigateToDossier = onNavigateToDossier,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sélectionnez une conversation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    } else {
        // Mobile navigation
        if (selectedConversation == null) {
            AdminInboxScreen(
                conversations = conversations,
                onConversationClick = { 
                    selectedConversation = it
                    viewModel.setConversation(it.id)
                },
                modifier = modifier
            )
        } else {
            AdminConversationDetailScreen(
                conversation = selectedConversation!!,
                onBack = { selectedConversation = null },
                onNavigateToDossier = onNavigateToDossier,
                viewModel = viewModel,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AdminInboxScreen(
    conversations: List<Conversation>,
    onConversationClick: (Conversation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inbox Admin", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tri: messages non lus + dossiers bloqués en premier
            val sortedConversations = conversations.sortedWith(compareByDescending<Conversation> { it.unreadCount > 0 }.thenBy { it.projectName })

            if (sortedConversations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucune conversation.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(sortedConversations) { conv ->
                    val indicator = when {
                        conv.unreadCount > 0 -> "🔴 Action requise"
                        conv.projectName.contains("Bloqué") -> "🟡 En attente réponse"
                        else -> ""
                    }
                    
                    ConversationItem(
                        dossierType = conv.clientName,
                        lastMessage = conv.lastMessage,
                        timestamp = "10:30",
                        hasUnread = conv.unreadCount > 0,
                        status = indicator.ifEmpty { "En cours" },
                        onClick = { onConversationClick(conv) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConversationDetailScreen(
    conversation: Conversation,
    onBack: () -> Unit,
    onNavigateToDossier: (String) -> Unit,
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(conversation.clientName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    TextButton(onClick = { onNavigateToDossier(conversation.id) }) {
                        Text("Ouvrir le dossier")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Message système
                item {
                    MessageBubble(
                        text = "Dossier créé",
                        sender = MessageSender.SYSTEM,
                        timestamp = conversation.time
                    )
                }

                // Messages de la conversation
                items(messages) { msg ->
                    MessageBubble(
                        text = msg.text,
                        sender = if (msg.isFromMe) MessageSender.ADMIN else MessageSender.CLIENT,
                        timestamp = msg.timestamp.toString()
                    )
                }
            }

            // Actions rapides Admin
            Divider()
            AdminQuickActions(
                onRequestDocument = {
                    viewModel.sendMessage("📄 Demande de document : Veuillez fournir...")
                },
                onRequestCorrection = {
                    viewModel.sendMessage("✏️ Demande de correction : Veuillez modifier...")
                },
                onValidateStep = {
                    viewModel.sendMessage("✅ Étape validée. Vous pouvez continuer.")
                },
                modifier = Modifier.padding(16.dp)
            )

            // Zone de réponse
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Attach file */ }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Joindre")
                }
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Écrire un message...") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Envoyer")
                }
            }
        }
    }
}
