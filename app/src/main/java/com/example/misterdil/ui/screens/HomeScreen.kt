package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.data.remote.AdminProfile
import com.example.misterdil.ui.components.DossierProgressBar
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DossierViewModel,
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {}
) {
    val dossiers by viewModel.dossiers.collectAsState()
    val admins by viewModel.admins.collectAsState()
    val selectedAdminId by viewModel.selectedAdminId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Misterdil", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Tableau de bord",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Admin Selection
            item {
                AdminSelectionSection(
                    admins = admins,
                    selectedAdminId = selectedAdminId,
                    onSelectAdmin = { viewModel.selectAdmin(it) }
                )
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard("Nouveau", Icons.Default.Add, Modifier.weight(1f)) { onNavigateTo("dossier") }
                    QuickActionCard("Message", Icons.AutoMirrored.Filled.Chat, Modifier.weight(1f)) { onNavigateTo("messagerie") }
                    QuickActionCard("Payer", Icons.Default.Payment, Modifier.weight(1f)) { onNavigateTo("paiement") }
                }
            }

            item {
                Text(text = "Dossiers en cours", style = MaterialTheme.typography.titleMedium)
            }

            if (dossiers.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Aucun dossier trouvé.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                items(dossiers) { dossier ->
                    DossierProgressCard(dossier, onClick = {
                        viewModel.navigateToDossier(dossier.id)
                        onNavigateTo("dossier/${dossier.id}")
                    })
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun AdminSelectionSection(
    admins: List<AdminProfile>,
    selectedAdminId: String?,
    onSelectAdmin: (String) -> Unit
) {
    val adminColors = listOf(Color(0xFF1565C0), Color(0xFF2E7D32))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Choisissez votre conseiller", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (admins.isEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) { i ->
                    AdminCard(name = "Conseiller ${i + 1}", index = i, color = adminColors.getOrElse(i) { Color.Gray }, selected = false, onClick = {})
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(admins.size) { i ->
                    val admin = admins[i]
                    AdminCard(
                        name = admin.name,
                        index = i,
                        color = adminColors.getOrElse(i) { Color(0xFF6A1B9A) },
                        selected = selectedAdminId == admin.id,
                        onClick = { onSelectAdmin(admin.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminCard(name: String, index: Int, color: Color, selected: Boolean, onClick: () -> Unit) {
    val initial = name.firstOrNull()?.toString() ?: "A"
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.width(130.dp).then(if (selected) Modifier.border(2.dp, color, MaterialTheme.shapes.medium) else Modifier)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
                Text(text = initial, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun QuickActionCard(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    ElevatedCard(onClick = onClick, modifier = modifier.height(100.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun DossierProgressCard(dossier: Dossier, onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(dossier.type, style = MaterialTheme.typography.titleLarge)
                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(text = dossier.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Utilisation du composant partagé pour corriger le bug 2000%
            DossierProgressBar(progress = dossier.progress)
        }
    }
}
