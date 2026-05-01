package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPaymentScreen(
    viewModel: PaymentViewModel,
    onBack: () -> Unit,
    onNavigateToDossier: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showConfirmation by remember { mutableStateOf(false) }
    var transactionId by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paiement du dossier", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        if (showConfirmation) {
            PaymentConfirmationScreen(
                transactionId = transactionId,
                onDownloadReceipt = {},
                onBackToDossier = onNavigateToDossier,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "Aucun paiement en attente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Vos paiements apparaîtront ici lorsque votre conseiller vous enverra une facture.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onNavigateToDossier) {
                        Text("Voir mes dossiers")
                    }
                }
            }
        }
    }
}

@Composable
fun DossierPaymentHeader(
    dossierType: String,
    dossierId: String,
    dossierStatus: String
) {
    Column {
        Text(
            dossierType,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                dossierId,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            StatusBadge(dossierStatus)
        }
    }
}

@Composable
fun PaymentConfirmationScreen(
    transactionId: String,
    onDownloadReceipt: () -> Unit,
    onBackToDossier: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success icon
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Paiement réussi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Votre paiement a bien été reçu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Numéro de transaction",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    transactionId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = onDownloadReceipt,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Télécharger le reçu")
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onBackToDossier,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retour au dossier")
        }
    }
}
