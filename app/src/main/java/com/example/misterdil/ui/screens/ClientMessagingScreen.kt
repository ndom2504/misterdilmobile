package com.example.misterdil.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.misterdil.utils.FILE_MSG_PREFIX
import com.example.misterdil.utils.getFileName
import kotlinx.coroutines.launch
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
fun ClientMessagingScreen(
    viewModel: ChatViewModel,
    onNavigateToDossier: (String) -> Unit = {},
    onNavigateToPaiement: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    val conversations by viewModel.conversations.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet) {
        Row(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.width(300.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surface)
            ) {
                ConversationListScreen(
                    conversations = conversations,
                    onConversationClick = { 
                        selectedConversation = it
                        viewModel.setConversation(it.id)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            VerticalDivider()
            if (selectedConversation != null) {
                ConversationDetailScreen(
                    conversation = selectedConversation!!,
                    onBack = { selectedConversation = null },
                    onNavigateToDossier = onNavigateToDossier,
                    onNavigateToPaiement = onNavigateToPaiement,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sélectionnez une conversation", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    } else {
        if (selectedConversation == null) {
            ConversationListScreen(
                conversations = conversations,
                onConversationClick = { 
                    selectedConversation = it
                    viewModel.setConversation(it.id)
                },
                modifier = modifier
            )
        } else {
            ConversationDetailScreen(
                conversation = selectedConversation!!,
                onBack = { selectedConversation = null },
                onNavigateToDossier = onNavigateToDossier,
                onNavigateToPaiement = onNavigateToPaiement,
                viewModel = viewModel,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    conversations: List<Conversation>,
    onConversationClick: (Conversation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Messagerie", fontWeight = FontWeight.Bold) }) },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (conversations.isEmpty()) {
                item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Aucune conversation.")
                }}
            } else {
                items(conversations) { conv ->
                    ConversationItem(
                        dossierType = conv.projectName,
                        lastMessage = conv.lastMessage,
                        timestamp = conv.time,
                        hasUnread = conv.unreadCount > 0,
                        status = "Conseiller",
                        avatarUrl = conv.avatarUrl,
                        onClick = { onConversationClick(conv) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDetailScreen(
    conversation: Conversation,
    onBack: () -> Unit,
    onNavigateToDossier: (String) -> Unit,
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
    onNavigateToPaiement: (() -> Unit)? = null
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Detect payment messages from admin
    LaunchedEffect(messages) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage != null && !lastMessage.isFromMe && lastMessage.text.startsWith("__PAYMENT__:")) {
            onNavigateToPaiement?.invoke()
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    val fileUrl = viewModel.uploadFile(it)
                    viewModel.sendMessage("$FILE_MSG_PREFIX$fileUrl")
                } catch (e: Exception) {
                    // Fallback: send filename only
                    val fileName = getFileName(context, it)
                    viewModel.sendMessage("$FILE_MSG_PREFIX$fileName")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(conversation.projectName, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { TextButton(onClick = { onNavigateToDossier(conversation.id) }) { Text("Voir le dossier") } }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(
                        text = msg.text,
                        sender = if (msg.isFromMe) MessageSender.CLIENT else MessageSender.ADMIN,
                        timestamp = "",
                        avatarUrl = if (!msg.isFromMe) conversation.avatarUrl else null,
                        onPay = onNavigateToPaiement
                    )
                }
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) { Icon(Icons.Default.AttachFile, null) }
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Écrire...") },
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { if (messageText.isNotBlank()) { viewModel.sendMessage(messageText); messageText = "" } }) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }
        }
    }
}
