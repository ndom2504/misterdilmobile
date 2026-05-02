package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
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
    dossierViewModel: DossierViewModel? = null,
    onNavigateToChat: (String) -> Unit = {}
) {
    val isEditable = dossier.status.lowercase() != "soumis" && dossier.status.lowercase() != "complété"
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val timelineSteps = listOf(
        TimelineStep("Informations", TimelineStepStatus.COMPLETED),
        TimelineStep("Documents", when {
            dossier.status.lowercase() == "en attente" -> TimelineStepStatus.IN_PROGRESS
            dossier.status.lowercase() == "soumis" || dossier.status.lowercase() == "complété" -> TimelineStepStatus.COMPLETED
            else -> TimelineStepStatus.PENDING
        }),
        TimelineStep("Analyse", when {
            dossier.status.lowercase() == "soumis" -> TimelineStepStatus.IN_PROGRESS
            dossier.status.lowercase() == "complété" -> TimelineStepStatus.COMPLETED
            else -> TimelineStepStatus.LOCKED
        })
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Dossier", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(dossier.type, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        StatusBadge(dossier.status)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Fix progression 2000%
                    val displayProgress = if (dossier.progress > 1f) dossier.progress / 100f else dossier.progress
                    DossierProgressBar(progress = displayProgress)
                }
            }

            item {
                Text("État d'avancement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                DossierTimeline(steps = timelineSteps)
            }

            item {
                Column {
                    Text("Documents requis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    DocumentItem(
                        label = "Passeport",
                        // Statut intelligent au lieu de validé par défaut
                        status = when(dossier.status.lowercase()) {
                            "complété" -> DocumentStatus.VALIDATED
                            "soumis" -> DocumentStatus.PENDING_VALIDATION
                            else -> DocumentStatus.NOT_PROVIDED
                        },
                        showActions = isEditable
                    )
                }
            }

            item {
                ElevatedCard(onClick = { onNavigateToChat(dossier.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contact conseiller", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Une question sur votre dossier ? Envoyez un message.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { onNavigateToChat(dossier.id) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Ouvrir la messagerie")
                        }
                    }
                }
            }

            // Affichage du paiement uniquement si nécessaire
            if (dossier.status.lowercase() == "complété") {
                item {
                    Column {
                        Text("Facturation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        PaymentCard(amount = "À définir", status = PaymentStatus.PENDING, onPay = {})
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
