package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.FieldType
import com.example.misterdil.data.models.FormField
import com.example.misterdil.ui.viewmodels.CreateDossierState
import com.example.misterdil.ui.viewmodels.DossierViewModel

private val dossierTypes = listOf(
    "Entrée Express",
    "Permis d'études",
    "Plan d'affaires",
    "Regroupement familial",
    "Visa visiteur",
    "Résidence permanente"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDossierScreen(
    viewModel: DossierViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(dossierTypes[0]) }
    val formFields = remember(selectedType) {
        mutableStateMapOf<String, String>().apply {
            getTemplateForType(selectedType).forEach { put(it.id, "") }
        }
    }
    val createState by viewModel.createState.collectAsState()

    LaunchedEffect(createState) {
        if (createState is CreateDossierState.Success) {
            viewModel.resetCreateState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau dossier", fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Type de dossier",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dossierTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) }
                        )
                    }
                }
            }

            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Informations requises — $selectedType",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(getTemplateForType(selectedType)) { field ->
                CreateFormField(
                    field = field,
                    value = formFields[field.id] ?: "",
                    onValueChange = { formFields[field.id] = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (createState is CreateDossierState.Error) {
                    Text(
                        (createState as CreateDossierState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = {
                        viewModel.createDossier(
                            type = selectedType,
                            formData = formFields.toMap()
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = createState !is CreateDossierState.Loading
                ) {
                    if (createState is CreateDossierState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Soumettre le dossier", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CreateFormField(field: FormField, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = field.label + if (field.required) " *" else "",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        when (field.type) {
            FieldType.NUMBER -> OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            FieldType.DATE -> OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("JJ/MM/AAAA") },
                singleLine = true
            )
            else -> OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
