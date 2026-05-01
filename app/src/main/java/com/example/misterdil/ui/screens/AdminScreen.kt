package com.example.misterdil.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import com.example.misterdil.ui.viewmodels.AuthViewModel
import com.example.misterdil.ui.viewmodels.ChatViewModel

const val PAYMENT_MSG_PREFIX = "__PAYMENT__:"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val userName by authViewModel.userName.collectAsState()
    val conversations by chatViewModel.conversations.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    var selectedConvId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { chatViewModel.refreshConversations() }

    if (selectedConvId == null) {
        AdminDashboard(
            userName = userName,
            conversations = conversations,
            modifier = modifier,
            onSelectClient = { id ->
                selectedConvId = id
                chatViewModel.setConversation(id)
            }
        )
    } else {
        val conv = conversations.find { it.id == selectedConvId }
        AdminClientDetailScreen(
            conversation = conv,
            messages = messages,
            onSendMessage = { chatViewModel.sendMessage(it) },
            onBack = { selectedConvId = null },
            modifier = modifier
        )
    }
}

// ─── Dashboard ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboard(
    userName: String?,
    conversations: List<Conversation>,
    modifier: Modifier,
    onSelectClient: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Espace Conseiller", fontWeight = FontWeight.Bold) })
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (userName?.firstOrNull() ?: 'A').toString().uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Bonjour, ${userName ?: "Conseiller"}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Conseiller immigration", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            item {
                // Stats
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatChip("Clients", conversations.size.toString(), MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
                    StatChip("En attente", conversations.count { it.lastMessage == "Dossier soumis" }.toString(), MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
                    StatChip("Actifs", conversations.count { it.unreadCount > 0 }.toString(), MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
                }
            }

            item {
                Text(
                    "Mes clients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (conversations.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(8.dp))
                            Text("Aucun client assigné pour l'instant.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            } else {
                items(conversations) { conv ->
                    ClientCard(conv = conv, onClick = { onSelectClient(conv.id) })
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ClientCard(conv: Conversation, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    conv.clientName.firstOrNull()?.toString()?.uppercase() ?: "C",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(conv.clientName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(conv.projectName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(conv.lastMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(conv.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                if (conv.unreadCount > 0) {
                    Spacer(Modifier.height(4.dp))
                    Badge { Text(conv.unreadCount.toString()) }
                }
            }
        }
    }
}

// ─── Client Detail (Admin Chat) ───────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminClientDetailScreen(
    conversation: Conversation?,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textState by remember { mutableStateOf("") }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var paymentAmount by remember { mutableStateOf("") }
    var paymentDesc by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(context, it)
            onSendMessage("$FILE_MSG_PREFIX$fileName")
        }
    }

    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Demande de paiement", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = paymentAmount,
                        onValueChange = { paymentAmount = it },
                        label = { Text("Montant (CAD)") },
                        leadingIcon = { Text("$", modifier = Modifier.padding(start = 12.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = paymentDesc,
                        onValueChange = { paymentDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (paymentAmount.isNotBlank()) {
                        val desc = paymentDesc.ifBlank { "Frais de service" }
                        onSendMessage("$PAYMENT_MSG_PREFIX$paymentAmount:$desc")
                        showPaymentDialog = false
                        paymentAmount = ""
                        paymentDesc = ""
                    }
                }) { Text("Envoyer") }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) { Text("Annuler") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(conversation?.clientName ?: "Client", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(conversation?.projectName ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column {
                    // Action chips row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                            label = { Text("Fichier", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        AssistChip(
                            onClick = { showPaymentDialog = true },
                            label = { Text("Paiement", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        AssistChip(
                            onClick = { textState = "📋 Instructions : " },
                            label = { Text("Instructions", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    // Text input row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp).imePadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = textState,
                            onValueChange = { textState = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Message au client...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
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
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Envoyer",
                                tint = if (textState.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message)
            }
        }
    }
}
