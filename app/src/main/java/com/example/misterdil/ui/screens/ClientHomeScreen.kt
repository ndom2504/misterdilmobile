package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.ui.components.ChecklistItem
import com.example.misterdil.ui.components.ChecklistStatus
import com.example.misterdil.ui.components.MainDossierCard
import com.example.misterdil.ui.components.StatusBadge
import com.example.misterdil.ui.viewmodels.AuthViewModel
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    authViewModel: AuthViewModel,
    dossierViewModel: DossierViewModel,
    userName: String?,
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {}
) {
    val dossiers by dossierViewModel.dossiers.collectAsState()
    val activeDossiers = dossiers.filter { it.status != "Complété" }
    val photoUri by authViewModel.photoUri.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accueil", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { onNavigateTo("profil") }) {
                        if (photoUri != null) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Profil",
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = "Profil")
                        }
                    }
                    IconButton(onClick = { dossierViewModel.refresh() }) {
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
                    // Header Client
                    ClientHeader(
                        userName = userName,
                        photoUri = photoUri,
                        activeDossiersCount = activeDossiers.size,
                        hasPendingAction = dossiers.any { it.status == "En attente" || it.progress < 1.0f }
                    )

                    // Carte "Mon dossier principal"
                    if (dossiers.isNotEmpty()) {
                        val mainDossier = dossiers.first()
                        MainDossierCard(
                            dossierType = mainDossier.type,
                            status = mainDossier.status,
                            progress = mainDossier.progress,
                            ctaText = getCTAForDossier(mainDossier),
                            onCtaClick = { onNavigateTo("dossier/${mainDossier.id}") }
                        )
                    } else {
                        CreateDossierPlaceholder(onClick = { onNavigateTo("create_dossier") })
                    }

                    // Raccourcis secondaires
                    SecondaryShortcuts(onNavigateTo = onNavigateTo)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Checklist rapide
                    QuickChecklist(
                        dossiers = dossiers,
                        onNavigateTo = onNavigateTo
                    )

                    // Messages récents
                    RecentMessagesSection(onNavigateTo = onNavigateTo)
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
                // Header Client
                item {
                    ClientHeader(
                        userName = userName,
                        photoUri = photoUri,
                        activeDossiersCount = activeDossiers.size,
                        hasPendingAction = dossiers.any { it.status == "En attente" || it.progress < 1.0f }
                    )
                }

                // Carte "Mon dossier principal"
                if (dossiers.isNotEmpty()) {
                    val mainDossier = dossiers.first()
                    item {
                        MainDossierCard(
                            dossierType = mainDossier.type,
                            status = mainDossier.status,
                            progress = mainDossier.progress,
                            ctaText = getCTAForDossier(mainDossier),
                            onCtaClick = { onNavigateTo("dossier/${mainDossier.id}") }
                        )
                    }
                } else {
                    item {
                        CreateDossierPlaceholder(onClick = { onNavigateTo("create_dossier") })
                    }
                }

                // Checklist rapide
                item {
                    QuickChecklist(
                        dossiers = dossiers,
                        onNavigateTo = onNavigateTo
                    )
                }

                // Messages récents
                item {
                    RecentMessagesSection(onNavigateTo = onNavigateTo)
                }

                // Raccourcis secondaires
                item {
                    SecondaryShortcuts(onNavigateTo = onNavigateTo)
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ClientHeader(
    userName: String?,
    photoUri: String?,
    activeDossiersCount: Int,
    hasPendingAction: Boolean
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
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    (userName?.firstOrNull() ?: 'U').toString().uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "Bonjour, ${userName ?: "Utilisateur"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Dossiers actifs : $activeDossiersCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (hasPendingAction) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text("Action requise", style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CreateDossierPlaceholder(onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AddCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Créer mon premier dossier",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Commencer votre demande d'immigration",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickChecklist(
    dossiers: List<Dossier>,
    onNavigateTo: (String) -> Unit
) {
    if (dossiers.isEmpty()) return

    Column {
        Text(
            "À faire",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        val mainDossier = dossiers.first()
        val items = mutableListOf<Pair<String, ChecklistStatus>>()

        when (mainDossier.status.lowercase()) {
            "en attente" -> {
                items.add("Compléter les informations" to ChecklistStatus.PENDING)
                items.add("Téléverser les documents" to ChecklistStatus.BLOCKED)
            }
            "en cours" -> {
                items.add("Informations complétées" to ChecklistStatus.COMPLETED)
                items.add("Documents en attente" to ChecklistStatus.PENDING)
            }
            "soumis" -> {
                items.add("Dossier soumis" to ChecklistStatus.COMPLETED)
                items.add("En attente de validation" to ChecklistStatus.PENDING)
            }
            "complété" -> {
                items.add("Dossier complété" to ChecklistStatus.COMPLETED)
            }
        }

        items.forEach { (label, status) ->
            ChecklistItem(
                label = label,
                status = status,
                onClick = { onNavigateTo("dossier/${mainDossier.id}") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecentMessagesSection(onNavigateTo: (String) -> Unit) {
    ElevatedCard(
        onClick = { onNavigateTo("messagerie") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Messagerie",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "Accéder à vos conversations",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SecondaryShortcuts(onNavigateTo: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShortcutCard(
            label = "Mes dossiers",
            icon = Icons.Default.Folder,
            onClick = { onNavigateTo("dossier") },
            modifier = Modifier.weight(1f)
        )
        ShortcutCard(
            label = "Messagerie",
            icon = Icons.AutoMirrored.Filled.Chat,
            onClick = { onNavigateTo("messagerie") },
            modifier = Modifier.weight(1f)
        )
        ShortcutCard(
            label = "Profil",
            icon = Icons.Default.Person,
            onClick = { onNavigateTo("profil") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ShortcutCard(
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

fun getCTAForDossier(dossier: Dossier): String {
    return when (dossier.status.lowercase()) {
        "en attente" -> "Continuer mon dossier"
        "en cours" -> "Voir les documents"
        "soumis" -> "Dossier en traitement"
        "complété" -> "Voir mon dossier"
        else -> "Continuer"
    }
}
