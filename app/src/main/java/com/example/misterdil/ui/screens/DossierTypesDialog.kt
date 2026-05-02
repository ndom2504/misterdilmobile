package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class DossierType(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DossierTypesDialog(
    onDismiss: () -> Unit,
    onSave: (List<DossierType>) -> Unit
) {
    var types by remember {
        mutableStateOf(
            listOf(
                DossierType("type-1", "Permis d'études", "Demande de permis d'études canadien", "500 CAD"),
                DossierType("type-2", "Entrée Express", "Profil d'immigration permanente", "1200 CAD"),
                DossierType("type-3", "Visa Visiteur", "Séjour temporaire touristique", "150 CAD")
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Types de dossiers", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                Text("Gérez les types de dossiers disponibles et leurs prix de base.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(types) { type ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(type.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(type.basePrice, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { types = types.filter { it.id != type.id } }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                TextButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Ajouter un type")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(types); onDismiss() }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
