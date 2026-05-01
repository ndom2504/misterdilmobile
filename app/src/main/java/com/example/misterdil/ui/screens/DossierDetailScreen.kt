package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.data.models.FieldType
import com.example.misterdil.data.models.FormField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DossierDetailScreen(
    dossier: Dossier,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditable = dossier.status.lowercase() != "soumis"

    // Dans une vraie app, ces champs viendraient d'une API ou d'une DB
    val formFields = remember { mutableStateListOf<FormField>().apply {
        addAll(getTemplateForType(dossier.type))
    } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails du Dossier", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    if (isEditable) {
                        IconButton(onClick = { /* Sauvegarder les modifications */ }) {
                            Icon(Icons.Default.Save, contentDescription = "Enregistrer")
                        }
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Informations : ${dossier.clientName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Type : ${dossier.type}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(
                        containerColor = when (dossier.status.lowercase()) {
                            "soumis" -> MaterialTheme.colorScheme.tertiary
                            "en attente" -> MaterialTheme.colorScheme.secondary
                            "en cours" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline
                        }
                    ) {
                        Text(dossier.status, style = MaterialTheme.typography.labelSmall)
                    }
                    if (!isEditable) {
                        Spacer(Modifier.width(8.dp))
                        Text("Dossier verrouillé", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }

            items(formFields) { field ->
                DynamicField(field = field, onValueChange = { newValue ->
                    if (isEditable) {
                        val index = formFields.indexOf(field)
                        if (index != -1) {
                            formFields[index] = field.copy(value = newValue)
                        }
                    }
                }, enabled = isEditable)
            }

            if (isEditable) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { /* Action finale */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Soumettre le dossier")
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Dossier soumis", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text("Votre dossier est en cours de traitement par le conseiller. Contactez-le via la messagerie pour toute question.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicField(field: FormField, onValueChange: (String) -> Unit, enabled: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = field.label + if (field.required) " *" else "",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (field.type) {
            FieldType.TEXT -> {
                OutlinedTextField(
                    value = field.value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Saisir ici...") },
                    enabled = enabled
                )
            }
            FieldType.NUMBER -> {
                OutlinedTextField(
                    value = field.value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0") },
                    enabled = enabled
                )
            }
            FieldType.DATE -> {
                OutlinedTextField(
                    value = field.value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("JJ/MM/AAAA") },
                    enabled = enabled
                )
            }
            FieldType.DROPDOWN -> {
                OutlinedTextField(
                    value = field.value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Sélectionner une option") },
                    enabled = enabled
                )
            }
            else -> {
                // Handle other field types (TEXT_AREA, CHECKBOX, RADIO, MULTI_SELECT, FILE_UPLOAD, SECTION_HEADER, READ_ONLY)
                OutlinedTextField(
                    value = field.value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Saisir ici...") },
                    enabled = enabled
                )
            }
        }
    }
}

fun getTemplateForType(type: String): List<FormField> {
    return when (type) {
        "Études" -> listOf(
            FormField(id = "ed_1", label = "Établissement visé", type = FieldType.TEXT),
            FormField(id = "ed_2", label = "Niveau d'études actuel", type = FieldType.TEXT),
            FormField(id = "ed_3", label = "Date de début souhaitée", type = FieldType.DATE),
            FormField(id = "ed_4", label = "Budget disponible ($)", type = FieldType.NUMBER)
        )
        "Entrée express" -> listOf(
            FormField(id = "ee_1", label = "Score CRS estimé", type = FieldType.NUMBER),
            FormField(id = "ee_2", label = "Test de langue (Niveaux)", type = FieldType.TEXT),
            FormField(id = "ee_3", label = "Années d'expérience", type = FieldType.NUMBER),
            FormField(id = "ee_4", label = "Profession (CNP)", type = FieldType.TEXT)
        )
        "Plan d'affaires" -> listOf(
            FormField(id = "biz_1", label = "Nom de l'entreprise", type = FieldType.TEXT),
            FormField(id = "biz_2", label = "Secteur d'activité", type = FieldType.TEXT),
            FormField(id = "biz_3", label = "Investissement prévu ($)", type = FieldType.NUMBER),
            FormField(id = "biz_4", label = "Nombre d'emplois créés", type = FieldType.NUMBER)
        )
        "Regroupement familial" -> listOf(
            FormField(id = "rf_1", label = "Nom du répondant", type = FieldType.TEXT),
            FormField(id = "rf_2", label = "Lien de parenté", type = FieldType.TEXT),
            FormField(id = "rf_3", label = "Statut du répondant au Canada", type = FieldType.TEXT),
            FormField(id = "rf_4", label = "Date de naissance du répondant", type = FieldType.DATE)
        )
        "Visa visiteur" -> listOf(
            FormField(id = "vv_1", label = "But de la visite", type = FieldType.TEXT),
            FormField(id = "vv_2", label = "Durée prévue du séjour (jours)", type = FieldType.NUMBER),
            FormField(id = "vv_3", label = "Province de destination", type = FieldType.TEXT),
            FormField(id = "vv_4", label = "Fonds disponibles ($)", type = FieldType.NUMBER)
        )
        "Résidence permanente" -> listOf(
            FormField(id = "rp_1", label = "Catégorie visée", type = FieldType.TEXT),
            FormField(id = "rp_2", label = "Années de résidence au Canada", type = FieldType.NUMBER),
            FormField(id = "rp_3", label = "Province de résidence", type = FieldType.TEXT),
            FormField(id = "rp_4", label = "Score IELTS/TEF", type = FieldType.TEXT)
        )
        else -> listOf(
            FormField(id = "gen_1", label = "Commentaires additionnels", type = FieldType.TEXT, required = false)
        )
    }
}
