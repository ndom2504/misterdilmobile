package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Copy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDossierScreen(
    dossier: Dossier,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    dossierViewModel: DossierViewModel? = null
) {
    val isEditable = dossier.status.lowercase() != "soumis" && dossier.status.lowercase() != "complété"
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val timelineSteps = listOf(
        TimelineStep("Informations personnelles", TimelineStepStatus.COMPLETED),
        TimelineStep("Documents requis", when {
            dossier.status.lowercase() == "en attente" -> TimelineStepStatus.IN_PROGRESS
            dossier.status.lowercase() == "soumis" || dossier.status.lowercase() == "complété" -> TimelineStepStatus.COMPLETED
            else -> TimelineStepStatus.PENDING
        }),
        TimelineStep("Vérification", when {
            dossier.status.lowercase() == "soumis" -> TimelineStepStatus.IN_PROGRESS
            dossier.status.lowercase() == "complété" -> TimelineStepStatus.COMPLETED
            else -> TimelineStepStatus.LOCKED
        }),
        TimelineStep("Soumission", if (dossier.status.lowercase() == "soumis" || dossier.status.lowercase() == "complété") TimelineStepStatus.COMPLETED else TimelineStepStatus.LOCKED),
        TimelineStep("Traitement", if (dossier.status.lowercase() == "complété") TimelineStepStatus.COMPLETED else TimelineStepStatus.LOCKED)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dossier", fontWeight = FontWeight.Bold) },
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
                    DossierHeader(dossier = dossier)
                    Text(
                        "Progression",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DossierTimeline(steps = timelineSteps)
                    InformationSection(dossier = dossier, isEditable = isEditable)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DocumentsSection(dossier = dossier, isEditable = isEditable)
                    DossierMessagesSection(onNavigateToChat = {})
                    if (dossier.status.lowercase() == "soumis" || dossier.progress >= 0.5f) {
                        PaymentSection(dossier = dossier)
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
                // En-tête du dossier
                item {
                    DossierHeader(dossier = dossier)
                }

                // Timeline
                item {
                    Text(
                        "Progression",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DossierTimeline(steps = timelineSteps)
                }

                // Section Informations
                item {
                    InformationSection(
                        dossier = dossier,
                        isEditable = isEditable
                    )
                }

                // Section Documents
                item {
                    DocumentsSection(dossier = dossier, isEditable = isEditable)
                }

                // Section Messages liés
                item {
                    DossierMessagesSection(onNavigateToChat = {})
                }

                // Section Paiement (si applicable)
                if (dossier.status.lowercase() == "soumis" || dossier.progress >= 0.5f) {
                    item {
                        PaymentSection(dossier = dossier)
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun DossierHeader(dossier: Dossier) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                dossier.type,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            StatusBadge(dossier.status)
        }
        Spacer(modifier = Modifier.height(8.dp))
        DossierProgressBar(progress = dossier.progress)
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "ID: ${dossier.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            IconButton(onClick = { /* Copy ID */ }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Copy, contentDescription = "Copier", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun InformationSection(
    dossier: Dossier,
    isEditable: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Informations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (!isEditable) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Verrouillé",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        if (isEditable) {
            // Formulaire dynamique éditable
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoField(label = "Nom complet", value = dossier.clientName, enabled = true)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoField(label = "Email", value = "client@example.com", enabled = true)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoField(label = "Téléphone", value = "", enabled = true)
                }
            }
        } else {
            // Formulaire en lecture seule
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoField(label = "Nom complet", value = dossier.clientName, enabled = false)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoField(label = "Email", value = "client@example.com", enabled = false)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoField(label = "Téléphone", value = "", enabled = false)
                }
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String, enabled: Boolean) {
    Column {
        Text(
            label + " *",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            placeholder = { Text("Saisir ici...") },
            singleLine = true
        )
    }
}

@Composable
fun DocumentsSection(dossier: Dossier, isEditable: Boolean) {
    Column {
        Text(
            "Documents requis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        DocumentItem(
            label = "Passeport",
            status = DocumentStatus.VALIDATED,
            showActions = isEditable
        )
        Spacer(modifier = Modifier.height(8.dp))
        DocumentItem(
            label = "Preuve d'admission",
            status = if (dossier.status.lowercase() == "en attente") DocumentStatus.NOT_PROVIDED else DocumentStatus.PENDING_VALIDATION,
            showActions = isEditable
        )
        Spacer(modifier = Modifier.height(8.dp))
        DocumentItem(
            label = "Justificatif de domicile",
            status = if (dossier.status.lowercase() == "en attente") DocumentStatus.NOT_PROVIDED else DocumentStatus.PENDING_VALIDATION,
            showActions = isEditable
        )
    }
}

@Composable
fun DossierMessagesSection(onNavigateToChat: () -> Unit) {
    Column {
        Text(
            "Messages",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        ElevatedCard(
            onClick = onNavigateToChat,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Votre conseiller",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "Votre dossier est en cours de vérification. Je vous contacterai si des documents supplémentaires sont nécessaires.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onNavigateToChat,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Répondre")
                }
            }
        }
    }
}

@Composable
fun PaymentSection(dossier: Dossier) {
    Column {
        Text(
            "Paiement",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        PaymentCard(
            amount = "500 €",
            status = if (dossier.status.lowercase() == "complété") PaymentStatus.PAID else PaymentStatus.PENDING,
            onPay = {},
            isAdmin = false
        )
    }
}
