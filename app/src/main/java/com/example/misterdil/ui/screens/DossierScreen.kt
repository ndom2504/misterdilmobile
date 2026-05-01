package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DossierScreen(viewModel: DossierViewModel, modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf("Tous") }
    val filters = listOf("Tous", "Études", "Entrée express", "Plan d'affaires")
    val dossiers by viewModel.dossiers.collectAsState()
    
    // État pour gérer l'affichage de la liste ou du détail
    var selectedDossier by remember { mutableStateOf<Dossier?>(null) }

    if (selectedDossier == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mes Dossiers", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { /* Search */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Rechercher")
                        }
                        IconButton(onClick = { /* Filter */ }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtrer")
                        }
                    }
                )
            },
            modifier = modifier
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Filters Row
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

                // Dossiers List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val filteredDossiers = if (selectedFilter == "Tous") {
                        dossiers
                    } else {
                        dossiers.filter { it.type == selectedFilter }
                    }

                    if (filteredDossiers.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Aucun dossier trouvé.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        items(filteredDossiers) { dossier ->
                            DossierItemCard(dossier, onClick = { selectedDossier = dossier })
                        }
                    }
                }
            }
        }
    } else {
        // Affichage du formulaire dynamique
        DossierDetailScreen(
            dossier = selectedDossier!!,
            onBack = { selectedDossier = null },
            modifier = modifier
        )
    }
}

@Composable
fun DossierItemCard(dossier: Dossier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = dossier.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = dossier.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusBadge(dossier.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = dossier.type,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dernière mise à jour: ${dossier.lastUpdate}",
                    style = MaterialTheme.typography.labelSmall
                )
                TextButton(onClick = onClick) {
                    Text("Ouvrir")
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val containerColor = when (status) {
        "Actif" -> MaterialTheme.colorScheme.primaryContainer
        "En attente" -> MaterialTheme.colorScheme.tertiaryContainer
        "Complété" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
