package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.misterdil.data.models.*
import com.example.misterdil.data.remote.AdminProfile
import com.example.misterdil.ui.components.DynamicForm
import com.example.misterdil.ui.viewmodels.ConversationCreateState
import com.example.misterdil.ui.viewmodels.ChatViewModel
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

private val adminColors = listOf(Color(0xFF1565C0), Color(0xFF2E7D32))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDossierScreen(
    viewModel: DossierViewModel,
    chatViewModel: ChatViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(dossierTypes[0]) }
    val formSchema = remember(selectedType) {
        FormSchemas.getSchemaByDossierType(selectedType)
    }
    val formFields = remember(selectedType) {
        mutableStateMapOf<String, String>()
    }
    val createState by viewModel.createState.collectAsState()
    val convCreateState by chatViewModel.convCreateState.collectAsState()
    val admins by viewModel.admins.collectAsState()

    var dossierCreatedType by remember { mutableStateOf<String?>(null) }
    var dossierId by remember { mutableStateOf<String?>(null) }
    var selectedAdmin by remember { mutableStateOf<AdminProfile?>(null) }

    LaunchedEffect(createState) {
        if (createState is CreateDossierState.Success) {
            dossierCreatedType = selectedType
            dossierId = (createState as CreateDossierState.Success).dossierId
            viewModel.resetCreateState()
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
                title = {
                    Text(
                        if (dossierCreatedType != null) "Choisir un conseiller" else "Nouveau dossier",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->

        if (dossierCreatedType != null) {
            // ── Phase 2 : Admin selection ──────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Dossier créé avec succès !", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Type : $dossierCreatedType", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text("Sélectionnez votre conseiller :", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("Un message automatique lui sera envoyé.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }

                val displayAdmins = admins.ifEmpty {
                    listOf(
                        AdminProfile(1, "Admin 1", "info@misterdil.ca"),
                        AdminProfile(2, "Admin 2", "divinegismille@gmail.com")
                    )
                }

                items(displayAdmins) { admin ->
                    val idx = displayAdmins.indexOf(admin)
                    val color = adminColors.getOrElse(idx) { Color(0xFF6A1B9A) }
                    val isSelected = selectedAdmin?.id == admin.id
                    ElevatedCard(
                        onClick = { selectedAdmin = admin },
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (isSelected) Modifier.border(2.dp, color, MaterialTheme.shapes.medium) else Modifier)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    admin.name.first().toString(),
                                    color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(admin.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("Conseiller immigration", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color)
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    if (convCreateState is ConversationCreateState.Error) {
                        Text(
                            (convCreateState as ConversationCreateState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = {
                            val admin = selectedAdmin ?: return@Button
                            chatViewModel.createConversationForDossier(
                                adminId = admin.id.toString(),
                                adminName = admin.name,
                                dossierType = dossierCreatedType!!,
                                dossierId = dossierId
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = selectedAdmin != null && convCreateState !is ConversationCreateState.Loading
                    ) {
                        if (convCreateState is ConversationCreateState.Loading) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Confirmer et envoyer", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        } else {
            // ── Phase 1 : Form ─────────────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Type de dossier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dossierTypes) { type ->
                            FilterChip(selected = selectedType == type, onClick = { selectedType = type }, label = { Text(type) })
                        }
                    }
                }

                item {
                    if (formSchema != null) {
                        DynamicForm(
                            schema = formSchema,
                            onFieldValueChange = { fieldId, value ->
                                formFields[fieldId] = value
                            }
                        )
                    } else {
                        Text(
                            "Formulaire non disponible pour ce type de dossier",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (createState is CreateDossierState.Error) {
                        Text((createState as CreateDossierState.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = { 
                            if (formSchema != null) {
                                viewModel.createDossier(type = selectedType, formData = formFields.toMap())
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = formSchema != null && createState !is CreateDossierState.Loading
                    ) {
                        if (createState is CreateDossierState.Loading) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Soumettre le dossier", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
