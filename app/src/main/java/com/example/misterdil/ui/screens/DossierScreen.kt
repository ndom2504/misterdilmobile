package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.ui.components.StatusBadge
import com.example.misterdil.ui.viewmodels.ChatViewModel
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DossierScreen(viewModel: DossierViewModel, chatViewModel: ChatViewModel, modifier: Modifier = Modifier, isAdmin: Boolean = false) {
    var selectedFilter by remember { mutableStateOf("Tous") }
    val filters = listOf("Tous", "Entrée Express", "Permis d'études", "Plan d'affaires", "Regroupement familial", "Visa visiteur", "Résidence permanente")
    val dossiers by viewModel.dossiers.collectAsState()
    val targetDossierId by viewModel.targetDossierId.collectAsState()

    var selectedDossier by remember { mutableStateOf<Dossier?>(null) }
    var showCreate by remember { mutableStateOf(false) }

    // Gérer l'ouverture automatique depuis l'accueil
    LaunchedEffect(targetDossierId, dossiers) {
        if (targetDossierId != null && dossiers.isNotEmpty()) {
            val dossier = dossiers.find { it.id == targetDossierId }
            if (dossier != null) {
                selectedDossier = dossier
                viewModel.navigateToDossier(null) // Reset après ouverture
            }
        }
    }

    if (showCreate) {
        CreateDossierScreen(
            viewModel = viewModel,
            chatViewModel = chatViewModel,
            onBack = { showCreate = false },
            modifier = modifier
        )
        return
    }

    if (selectedDossier == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isAdmin) "Tous les dossiers" else "Mes Dossiers", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { /* Search */ }) { Icon(Icons.Default.Search, null) }
                        IconButton(onClick = { /* Filter */ }) { Icon(Icons.Default.FilterList, null) }
                    }
                )
            },
            floatingActionButton = {
                if (!isAdmin) {
                    FloatingActionButton(onClick = { showCreate = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nouveau")
                    }
                }
            },
            modifier = modifier
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val filteredDossiers = if (selectedFilter == "Tous") dossiers else dossiers.filter { it.type == selectedFilter }

                    if (filteredDossiers.isEmpty()) {
                        item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Aucun dossier trouvé.")
                        }}
                    } else {
                        items(filteredDossiers) { dossier ->
                            DossierItemCard(dossier, onClick = { selectedDossier = dossier })
                        }
                    }
                }
            }
        }
    } else {
        if (isAdmin) {
            AdminDossierScreen(dossier = selectedDossier!!, onBack = { selectedDossier = null }, modifier = modifier, dossierViewModel = viewModel)
        } else {
            ClientDossierScreen(dossier = selectedDossier!!, onBack = { selectedDossier = null }, modifier = modifier, dossierViewModel = viewModel)
        }
    }
}

@Composable
fun DossierItemCard(dossier: Dossier, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(dossier.clientName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusBadge(dossier.status)
            }
            Text(dossier.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Mise à jour : ${dossier.lastUpdate}", style = MaterialTheme.typography.labelSmall)
        }
    }
}
