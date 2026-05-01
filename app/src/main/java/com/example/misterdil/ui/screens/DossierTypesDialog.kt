package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class DossierType(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: String
)

@Composable
fun DossierTypesDialog(
    onDismiss: () -> Unit,
    onSave: (List<DossierType>) -> Unit
) {
    var dossierTypes by remember {
        mutableStateOf(
            listOf(
                DossierType("type-1", "Permis d'études", "Demande de permis d'études canadien", "500 €"),
                DossierType("type-2", "Entrée Express", "Programme Entrée Express", "750 €"),
                DossierType("type-3", "Plan d'affaires", "Création d'entreprise au Canada", "1000 €"),
                DossierType("type-4", "Regroupement familial", "Parrainage familial", "400 €"),
                DossierType("type-5", "Visa visiteur", "Visa touristique", "200 €"),
                DossierType("type-6", "Résidence permanente", "Demande de RP", "1200 €")
            )
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingType by remember { mutableStateOf<DossierType?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        "Types de dossiers",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(dossierTypes) { type ->
                        DossierTypeCard(
                            dossierType = type,
                            onEdit = { editingType = type },
                            onDelete = {
                                dossierTypes = dossierTypes.filter { it.id != type.id }
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ajouter un type")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { onSave(dossierTypes) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }

    if (showAddDialog || editingType != null) {
        EditDossierTypeDialog(
            dossierType = editingType,
            onDismiss = {
                showAddDialog = false
                editingType = null
            },
            onSave = { newType ->
                dossierTypes = if (editingType != null) {
                    dossierTypes.map { if (it.id == editingType!!.id) newType else it }
                } else {
                    dossierTypes + newType.copy(id = "type-${dossierTypes.size + 1}")
                }
                showAddDialog = false
                editingType = null
            }
        )
    }
}

@Composable
fun DossierTypeCard(
    dossierType: DossierType,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    dossierType.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    dossierType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    dossierType.basePrice,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDossierTypeDialog(
    dossierType: DossierType?,
    onDismiss: () -> Unit,
    onSave: (DossierType) -> Unit
) {
    var name by remember { mutableStateOf(dossierType?.name ?: "") }
    var description by remember { mutableStateOf(dossierType?.description ?: "") }
    var basePrice by remember { mutableStateOf(dossierType?.basePrice ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    if (dossierType != null) "Modifier le type" else "Ajouter un type",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = basePrice,
                    onValueChange = { basePrice = it },
                    label = { Text("Prix de base") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = { 
                            onSave(DossierType(dossierType?.id ?: "", name, description, basePrice))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
