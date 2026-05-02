package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.DossierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDossierScreen(
    dossier: Dossier,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    dossierViewModel: DossierViewModel? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion Dossier Admin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit dossier */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier")
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                AdminDossierHeader(dossier = dossier)
            }

            item {
                Text(
                    "Actions de validation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { /* Valider étape */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Valider")
                    }
                    OutlinedButton(
                        onClick = { /* Demander correction */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Correction")
                    }
                }
            }

            item {
                Text(
                    "Documents du client",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                DocumentItem(label = "Passeport", status = DocumentStatus.PENDING_VALIDATION, showActions = true)
                Spacer(modifier = Modifier.height(8.dp))
                DocumentItem(label = "Preuve d'admission", status = DocumentStatus.NOT_PROVIDED, showActions = true)
            }
            
            item {
                Text(
                    "Facturation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                PaymentCard(
                    amount = "À définir", // Devise CAD
                    status = PaymentStatus.PENDING,
                    isAdmin = true,
                    onViewDetails = {}
                )
            }
        }
    }
}

@Composable
fun AdminDossierHeader(dossier: Dossier) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!dossier.avatarUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = dossier.avatarUrl,
                                contentDescription = "Avatar client",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                dossier.clientName.firstOrNull()?.toString()?.uppercase() ?: "C",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Column {
                        Text(dossier.clientName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(dossier.type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                StatusBadge(dossier.status)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Fix bug 2000% pour l'admin aussi
            val displayProgress = if (dossier.progress > 1f) dossier.progress / 100f else dossier.progress
            DossierProgressBar(progress = displayProgress)
        }
    }
}
