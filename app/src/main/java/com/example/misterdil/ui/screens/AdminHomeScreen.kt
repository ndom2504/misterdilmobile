package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.ui.components.PriorityItemCard
import com.example.misterdil.ui.components.StatusBadge
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
    val dossiers by dossierViewModel.dossiers.collectAsState()
    val conversations by chatViewModel.conversations.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val blockedDossiers = dossiers.filter { it.status == "Bloqué" }
    val pendingPayments = dossiers.filter { it.status == "En attente" && it.progress < 0.5f }
    val activeDossiers = dossiers.filter { it.status == "En cours" || it.status == "Soumis" }
    val unreadConversations = conversations.filter { it.unreadCount > 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Admin", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { dossierViewModel.refresh(); chatViewModel.refreshConversations() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        if (isTablet) {
            // Tablet layout: 2 columns
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header Admin avec indicateurs clés
                    AdminHeader(
                        userName = userName,
                        blockedCount = blockedDossiers.size,
                        pendingPaymentsCount = pendingPayments.size,
                        activeCount = activeDossiers.size
                    )

                    // Actions rapides Admin
                    AdminQuickActions(onNavigateTo = onNavigateTo)

                    // Vue "À traiter en priorité"
                    if (blockedDossiers.isNotEmpty()) {
                        Text(
                            "À traiter en priorité",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        blockedDossiers.take(5).forEach { dossier ->
                            PriorityItemCard(
                                clientName = dossier.clientName,
                                dossierType = dossier.type,
                                reason = "Document manquant",
                                onValidate = { onNavigateTo("dossier/${dossier.id}") },
                                onContact = { onNavigateTo("messagerie") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else if (pendingPayments.isNotEmpty()) {
                        Text(
                            "Paiements en attente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        pendingPayments.take(5).forEach { dossier ->
                            PriorityItemCard(
                                clientName = dossier.clientName,
                                dossierType = dossier.type,
                                reason = "Paiement non effectué",
                                onValidate = { onNavigateTo("dossier/${dossier.id}") },
                                onContact = { onNavigateTo("messagerie") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Vue globale dossiers avec filtres
                    GlobalDossiersView(
                        dossiers = dossiers,
                        onNavigateTo = onNavigateTo
                    )

                    // Messages non lus
                    if (unreadConversations.isNotEmpty()) {
                        Text(
                            "Messages non lus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        unreadConversations.take(5).forEach { conv ->
                            UnreadMessageCard(
                                clientName = conv.clientName,
                                projectName = conv.projectName,
                                lastMessage = conv.lastMessage,
                                unreadCount = conv.unreadCount,
                                onClick = { onNavigateTo("messagerie/${conv.id}") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        } else {
            // Mobile layout: single column
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Admin avec indicateurs clés
                item {
                    AdminHeader(
                        userName = userName,
                        blockedCount = blockedDossiers.size,
                        pendingPaymentsCount = pendingPayments.size,
                        activeCount = activeDossiers.size
                    )
                }

                // Actions rapides Admin
                item {
                    AdminQuickActions(onNavigateTo = onNavigateTo)
                }

                // Vue "À traiter en priorité"
                if (blockedDossiers.isNotEmpty()) {
                    item {
                        Text(
                            "À traiter en priorité",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(blockedDossiers.take(5)) { dossier ->
                        PriorityItemCard(
                            clientName = dossier.clientName,
                            dossierType = dossier.type,
                            reason = "Document manquant",
                            onValidate = { onNavigateTo("dossier/${dossier.id}") },
                            onContact = { onNavigateTo("messagerie") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else if (pendingPayments.isNotEmpty()) {
                    item {
                        Text(
                            "Paiements en attente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(pendingPayments.take(5)) { dossier ->
                        PriorityItemCard(
                            clientName = dossier.clientName,
                            dossierType = dossier.type,
                            reason = "Paiement non effectué",
                            onValidate = { onNavigateTo("dossier/${dossier.id}") },
                            onContact = { onNavigateTo("messagerie") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Vue globale dossiers avec filtres
                item {
                    GlobalDossiersView(
                        dossiers = dossiers,
                        onNavigateTo = onNavigateTo
                    )
                }

                // Messages non lus
                if (unreadConversations.isNotEmpty()) {
                    item {
                        Text(
                            "Messages non lus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(unreadConversations.take(5)) { conv ->
                        UnreadMessageCard(
                            clientName = conv.clientName,
                            projectName = conv.projectName,
                            lastMessage = conv.lastMessage,
                            unreadCount = conv.unreadCount,
                            onClick = { onNavigateTo("messagerie/${conv.id}") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun AdminHeader(
    userName: String?,
    blockedCount: Int,
    pendingPaymentsCount: Int,
    activeCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                (userName?.firstOrNull() ?: 'A').toString().uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Bonjour, ${userName ?: "Admin"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (blockedCount > 0) {
                    AssistChip(
                        onClick = {},
                        label = { Text("🔴 $blockedCount Bloqués") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
                if (pendingPaymentsCount > 0) {
                    AssistChip(
                        onClick = {},
                        label = { Text("🟠 $pendingPaymentsCount Paiements") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text("🟢 $activeCount Actifs") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}

@Composable
fun AdminQuickActions(onNavigateTo: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionBtn(
            label = "Créer dossier",
            icon = Icons.Default.Add,
            onClick = { onNavigateTo("create_dossier") },
            modifier = Modifier.weight(1f)
        )
        QuickActionBtn(
            label = "Facture",
            icon = Icons.Default.Payment,
            onClick = { onNavigateTo("paiement") },
            modifier = Modifier.weight(1f)
        )
        QuickActionBtn(
            label = "Message",
            icon = Icons.AutoMirrored.Filled.Chat,
            onClick = { onNavigateTo("messagerie") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionBtn(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = modifier
    )
}

@Composable
fun GlobalDossiersView(
    dossiers: List<Dossier>,
    onNavigateTo: (String) -> Unit
) {
    Column {
        Text(
            "Vue globale",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text("Tous (${dossiers.size})") }
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text("En attente (${dossiers.count { it.status == "En attente" }})") }
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text("En cours (${dossiers.count { it.status == "En cours" }})") }
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text("Soumis (${dossiers.count { it.status == "Soumis" }})") }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (dossiers.isEmpty()) {
            Text(
                "Aucun dossier",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            dossiers.take(3).forEach { dossier ->
                MiniDossierCard(
                    dossier = dossier,
                    onClick = { onNavigateTo("dossier/${dossier.id}") }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (dossiers.size > 3) {
                TextButton(
                    onClick = { onNavigateTo("dossier") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Voir tous les dossiers")
                }
            }
        }
    }
}

@Composable
fun MiniDossierCard(
    dossier: Dossier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    dossier.clientName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    dossier.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            StatusBadge(dossier.status)
        }
    }
}

@Composable
fun UnreadMessageCard(
    clientName: String,
    projectName: String,
    lastMessage: String,
    unreadCount: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    clientName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    projectName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    lastMessage,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
            Badge(containerColor = MaterialTheme.colorScheme.error) {
                Text(unreadCount.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
