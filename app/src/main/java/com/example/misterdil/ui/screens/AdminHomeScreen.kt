package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.misterdil.ui.components.ConversationItem
import com.example.misterdil.ui.viewmodels.ChatViewModel
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    userName: String?,
    dossierViewModel: DossierViewModel,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {}
) {
    val conversations by chatViewModel.conversations.collectAsState()
    val dossiers by dossierViewModel.dossiers.collectAsState()
    
    // On pourrait aussi ajouter l'avatar admin ici si besoin
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Misterdil Admin", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { onNavigateTo("profil") }) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
                    }
                    IconButton(onClick = { dossierViewModel.refresh(); chatViewModel.refreshConversations() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text("Bonjour, $userName", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Voici le résumé de votre activité aujourd'hui.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard("Dossiers", dossiers.size.toString(), Modifier.weight(1f))
                    AdminStatCard("Nouveaux messages", conversations.count { it.unreadCount > 0 }.toString(), Modifier.weight(1f))
                }
            }

            item {
                Text("Messages récents", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (conversations.isEmpty()) {
                item { Text("Aucune conversation active.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary) }
            } else {
                items(conversations.take(5)) { conv ->
                    ConversationItem(
                        dossierType = conv.projectName,
                        lastMessage = conv.lastMessage,
                        timestamp = conv.time,
                        hasUnread = conv.unreadCount > 0,
                        status = "Client: ${conv.clientName}",
                        avatarUrl = conv.avatarUrl,
                        onClick = { onNavigateTo("messagerie") }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
