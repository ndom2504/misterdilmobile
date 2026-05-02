package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.misterdil.data.models.*
import com.example.misterdil.data.remote.AdminProfile
import com.example.misterdil.ui.components.DynamicForm
import com.example.misterdil.ui.viewmodels.ConversationCreateState
import com.example.misterdil.ui.viewmodels.ChatViewModel
import com.example.misterdil.ui.viewmodels.CreateDossierState
import com.example.misterdil.ui.viewmodels.DossierViewModel
import kotlinx.coroutines.delay

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
    chatViewModel: ChatViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(dossierTypes[0]) }
    val formSchema = remember(selectedType) { FormSchemas.getSchemaByDossierType(selectedType) }
    val formFields = remember(selectedType) { mutableStateMapOf<String, String>() }
    val createState by viewModel.createState.collectAsState()
    val convCreateState by chatViewModel.convCreateState.collectAsState()
    val admins by viewModel.admins.collectAsState()

    var dossierCreatedType by remember { mutableStateOf<String?>(null) }
    var dossierId by remember { mutableStateOf<String?>(null) }
    var selectedAdmin by remember { mutableStateOf<AdminProfile?>(null) }
    var autosaveTrigger by remember { mutableStateOf(0) }
    var showAdvisorSelection by remember { mutableStateOf(false) }

    // Autosave local
    LaunchedEffect(autosaveTrigger) {
        if (formFields.isNotEmpty()) {
            delay(2000)
            viewModel.saveDraft(selectedType, formFields.toMap())
        }
    }

    LaunchedEffect(formFields.size) { if (formFields.isNotEmpty()) autosaveTrigger++ }

    LaunchedEffect(createState) {
        val state = createState
        if (state is CreateDossierState.Success) {
            dossierId = state.dossierId
            selectedAdmin?.let { admin ->
                chatViewModel.createConversationForDossier(
                    adminId = admin.id,
                    adminName = admin.name,
                    dossierType = dossierCreatedType ?: selectedType,
                    dossierId = dossierId
                )
            }
            viewModel.resetCreateState()
            showAdvisorSelection = false
        }
    }

    LaunchedEffect(convCreateState) {
        if (convCreateState is ConversationCreateState.Success) {
            chatViewModel.resetConvCreateState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showAdvisorSelection) "Choisir un conseiller" else "Nouveau dossier", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (showAdvisorSelection) showAdvisorSelection = false else onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        if (showAdvisorSelection) {
            AdvisorScreen(
                viewModel = viewModel,
                onBack = { showAdvisorSelection = false },
                onAdvisorSelected = { admin ->
                    selectedAdmin = admin
                    dossierCreatedType = selectedType
                    viewModel.createDossier(type = selectedType, formData = formFields.toMap())
                }
            )
        } else if (dossierCreatedType != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Dossier validé !", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text("Votre demande va être transmise.", style = MaterialTheme.typography.bodySmall)
                }
                item {
                    Button(
                        onClick = {
                            val admin = selectedAdmin ?: return@Button
                            chatViewModel.createConversationForDossier(admin.id, admin.name, dossierCreatedType!!, dossierId)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = selectedAdmin != null && convCreateState !is ConversationCreateState.Loading
                    ) {
                        Text("Confirmer l'envoi au conseiller")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Type de dossier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dossierTypes) { type ->
                            FilterChip(selected = selectedType == type, onClick = { selectedType = type }, label = { Text(type) })
                        }
                    }
                }
                item {
                    if (formSchema != null) {
                        DynamicForm(schema = formSchema, onFieldValueChange = { id, v -> formFields[id] = v }, fieldValues = formFields.toMap())
                    }
                }
                item {
                    Button(
                        onClick = { if (formSchema != null) showAdvisorSelection = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = formSchema != null && createState !is CreateDossierState.Loading
                    ) {
                        Text("Suivant")
                    }
                }
            }
        }
    }
}
