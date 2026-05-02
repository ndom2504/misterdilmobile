package com.example.misterdil.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import com.example.misterdil.ui.components.ConversationItem
import com.example.misterdil.ui.viewmodels.ChatViewModel
import com.example.misterdil.utils.FILE_MSG_PREFIX
import com.example.misterdil.utils.getFileName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagerieScreen(viewModel: ChatViewModel, modifier: Modifier = Modifier, onNavigateToPaiement: (() -> Unit)? = null) {
    var selectedConversationId by remember { mutableStateOf<String?>(null) }
    val conversations by viewModel.conversations.collectAsState()
    val messages by viewModel.messages.collectAsState()

    if (selectedConversationId == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Messagerie", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { viewModel.refreshConversations() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                        }
                    }
                )
            },
            modifier = modifier
        ) { padding ->
            if (conversations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Aucune conversation.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    items(conversations) { conversation ->
                        // Utilisation du composant partagé
                        ConversationItem(
                            dossierType = conversation.projectName,
                            lastMessage = conversation.lastMessage,
                            timestamp = conversation.time,
                            hasUnread = conversation.unreadCount > 0,
                            status = "En cours",
                            avatarUrl = conversation.avatarUrl,
                            onClick = {
                                selectedConversationId = conversation.id
                                viewModel.setConversation(conversation.id)
                            }
                        )
                    }
                }
            }
        }
    } else {
        val selectedConversation = conversations.find { it.id == selectedConversationId }
        ChatDetailScreen(
            conversationName = selectedConversation?.clientName ?: "Discussion",
            messages = messages,
            onSendMessage = { viewModel.sendMessage(it) },
            onBack = { selectedConversationId = null },
            onNavigateToPaiement = onNavigateToPaiement,
            modifier = modifier,
            viewModel = viewModel
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversationName: String,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToPaiement: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel? = null
) {
    var textState by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Detect payment messages from admin and trigger payment flow
    LaunchedEffect(messages) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage != null && !lastMessage.isFromMe && lastMessage.text.startsWith("__PAYMENT__:")) {
            val paymentData = lastMessage.text.removePrefix("__PAYMENT__:")
            onNavigateToPaiement?.invoke(paymentData)
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            scope.launch {
                try {
                    if (viewModel != null) {
                        val fileUrl = viewModel.uploadFile(it)
                        onSendMessage("$FILE_MSG_PREFIX$fileUrl")
                    } else {
                        // Fallback: send filename only
                        val fileName = getFileName(context, it)
                        onSendMessage("$FILE_MSG_PREFIX$fileName")
                    }
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(conversationName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp).imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Joindre")
                    }
                    TextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        maxLines = 4
                    )
                    IconButton(
                        onClick = {
                            if (textState.isNotBlank()) {
                                onSendMessage(textState)
                                textState = ""
                            }
                        },
                        enabled = textState.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer")
                    }
                }
            }
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message, onPayRequest = onNavigateToPaiement)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, onPayRequest: (() -> Unit)? = null) {
    if (message.text.startsWith(FILE_MSG_PREFIX)) {
        val fileName = message.text.removePrefix(FILE_MSG_PREFIX)
        FileMessageBubble(fileName = fileName, isFromMe = message.isFromMe, avatarUrl = message.senderAvatar)
        return
    }
    if (message.text.startsWith(PAYMENT_MSG_PREFIX)) {
        val raw = message.text.removePrefix(PAYMENT_MSG_PREFIX)
        val parts = raw.split(":")
        val amount = parts.getOrNull(0) ?: "0"
        val desc = parts.getOrNull(1) ?: "Frais de service"
        PaymentRequestBubble(amount = amount, description = desc, isFromMe = message.isFromMe, onPay = onPayRequest)
        return
    }

    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val containerColor = if (message.isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (message.isFromMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isFromMe) {
            AvatarIcon(message.senderAvatar)
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                bottomEnd = if (message.isFromMe) 4.dp else 16.dp
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (message.isFromMe) {
            Spacer(Modifier.width(8.dp))
            AvatarIcon(message.senderAvatar) // Avatar "Moi"
        }
    }
}

@Composable
fun AvatarIcon(avatarUrl: String?) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrEmpty()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun FileMessageBubble(fileName: String, isFromMe: Boolean, avatarUrl: String?) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isFromMe) AvatarIcon(avatarUrl)
        Spacer(Modifier.width(8.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.clickable { 
                Toast.makeText(context, "Ouverture de $fileName...", Toast.LENGTH_SHORT).show()
            }
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.InsertDriveFile, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(fileName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
        
        if (isFromMe) {
            Spacer(Modifier.width(8.dp))
            AvatarIcon(avatarUrl)
        }
    }
}

@Composable
fun PaymentRequestBubble(amount: String, description: String, isFromMe: Boolean, onPay: (() -> Unit)?) {
    val alignment = if (isFromMe) Alignment.End else Alignment.Start
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        ElevatedCard(
            modifier = Modifier.widthIn(max = 260.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Demande de paiement", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$$amount CAD", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(description, style = MaterialTheme.typography.bodySmall)
                if (!isFromMe && onPay != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onPay, modifier = Modifier.fillMaxWidth()) {
                        Text("Payer maintenant")
                    }
                }
            }
        }
    }
}
