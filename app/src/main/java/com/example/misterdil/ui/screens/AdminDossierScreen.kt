package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDossierScreen(
    dossier: Dossier,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    dossierViewModel: DossierViewModel? = null
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(dossier.status) }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dossier Admin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
                    AdminDossierHeader(dossier = dossier, onStatusChange = { showStatusDialog = true })
                    QuickAnalysisSection(dossier = dossier)
                    FormValidationSection(dossier = dossier)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DocumentControlSection(dossier = dossier)
                    ContextualMessagingSection(onNavigateToChat = {})
                    BillingSection(dossier = dossier)
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
                // En-tête Admin
                item {
                    AdminDossierHeader(
                        dossier = dossier,
                        onStatusChange = { showStatusDialog = true }
                    )
                }

                // Vue Analyse rapide
                item {
                    QuickAnalysisSection(dossier = dossier)
                }

                // Formulaires (lecture + validation)
                item {
                    FormValidationSection(dossier = dossier)
                }

                // Documents (contrôle)
                item {
                    DocumentControlSection(dossier = dossier)
                }

                // Messagerie contextuelle
                item {
                    ContextualMessagingSection(onNavigateToChat = {})
                }

                // Facturation
                item {
                    BillingSection(dossier = dossier)
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showStatusDialog) {
        StatusChangeDialog(
            currentStatus = dossier.status,
            onConfirm = { newStatus ->
                selectedStatus = newStatus
                showStatusDialog = false
                // Update dossier status
            },
            onDismiss = { showStatusDialog = false }
        )
    }
}

@Composable
fun AdminDossierHeader(
    dossier: Dossier,
    onStatusChange: () -> Unit
) {
    Column {
        Text(
            dossier.clientName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            dossier.type,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(dossier.status)
            DossierProgressBar(progress = dossier.progress)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onStatusChange,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Changer le statut")
        }
    }
}

@Composable
fun QuickAnalysisSection(dossier: Dossier) {
    Column {
        Text(
            "Analyse rapide",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnalysisChip(
                label = "✓ Champs complétés",
                isValid = true,
                modifier = Modifier.weight(1f)
            )
            AnalysisChip(
                label = "❌ Documents manquants",
                isValid = false,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnalysisChip(
            label = "💳 Paiement en attente",
            isValid = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AnalysisChip(label: String, isValid: Boolean, modifier: Modifier = Modifier) {
    AssistChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isValid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
        )
    )
}

@Composable
fun FormValidationSection(dossier: Dossier) {
    Column {
        Text(
            "Validation des informations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ValidationField(label = "Nom complet", value = dossier.clientName, isValid = true)
                Spacer(modifier = Modifier.height(12.dp))
                ValidationField(label = "Email", value = "client@example.com", isValid = false, reason = "Format invalide")
                Spacer(modifier = Modifier.height(12.dp))
                ValidationField(label = "Téléphone", value = "", isValid = false, reason = "Champ requis")
            }
        }
    }
}

@Composable
fun ValidationField(label: String, value: String, isValid: Boolean, reason: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isValid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
            )
            if (!isValid && reason != null) {
                Text(
                    reason,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Icon(
            if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun DocumentControlSection(dossier: Dossier) {
    Column {
        Text(
            "Contrôle des documents",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        DocumentItem(
            label = "Passeport",
            status = DocumentStatus.VALIDATED,
            showActions = false
        )
        Spacer(modifier = Modifier.height(8.dp))
        DocumentItem(
            label = "Preuve d'admission",
            status = DocumentStatus.REJECTED,
            rejectionReason = "Document illisible",
            showActions = false
        )
        Spacer(modifier = Modifier.height(8.dp))
        DocumentItem(
            label = "Justificatif de domicile",
            status = DocumentStatus.PENDING_VALIDATION,
            showActions = false
        )
    }
}

@Composable
fun ContextualMessagingSection(onNavigateToChat: () -> Unit) {
    Column {
        Text(
            "Messagerie",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Demander une correction...") },
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToChat,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Voir conversation")
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Envoyer")
                    }
                }
            }
        }
    }
}

@Composable
fun BillingSection(dossier: Dossier) {
    Column {
        Text(
            "Facturation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        PaymentCard(
            amount = "500 €",
            status = PaymentStatus.PENDING,
            onViewDetails = {},
            isAdmin = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Générer facture")
        }
    }
}

@Composable
fun StatusChangeDialog(
    currentStatus: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf("En attente", "En cours", "Soumis", "Complété")
    var selected by remember { mutableStateOf(currentStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Changer le statut") },
        text = {
            Column {
                Text("Nouveau statut :")
                Spacer(modifier = Modifier.height(12.dp))
                options.forEach { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == status,
                            onClick = { selected = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selected) }) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
