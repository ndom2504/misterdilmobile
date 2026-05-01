package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPaymentScreen(
    viewModel: PaymentViewModel,
    onBack: () -> Unit,
    onNavigateToDossier: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var invoiceAmount by remember { mutableStateOf("") }
    var invoiceDescription by remember { mutableStateOf("") }
    var showGenerateDialog by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val paymentHistory by remember { mutableStateOf(listOf<PaymentHistoryItem>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paiements Admin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Refresh */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        if (isTablet) {
            // Tablet layout: récap gauche / historique droite
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Générer une facture",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = invoiceAmount,
                                onValueChange = { invoiceAmount = it },
                                label = { Text("Montant (CAD)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = invoiceDescription,
                                onValueChange = { invoiceDescription = it },
                                label = { Text("Description") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showGenerateDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = invoiceAmount.isNotBlank() && invoiceDescription.isNotBlank()
                            ) {
                                Text("Générer facture")
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Historique des paiements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (paymentHistory.isEmpty()) {
                        Text(
                            "Aucun paiement enregistré",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        paymentHistory.forEach { item ->
                            PaymentHistoryItemCard(item = item)
                        }
                    }
                }
            }
        } else {
            // Mobile layout
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Générer une facture",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = invoiceAmount,
                                onValueChange = { invoiceAmount = it },
                                label = { Text("Montant (CAD)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = invoiceDescription,
                                onValueChange = { invoiceDescription = it },
                                label = { Text("Description") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showGenerateDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = invoiceAmount.isNotBlank() && invoiceDescription.isNotBlank()
                            ) {
                                Text("Générer facture")
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Historique des paiements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (paymentHistory.isEmpty()) {
                    item {
                        Text(
                            "Aucun paiement enregistré",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(paymentHistory) { item ->
                        PaymentHistoryItemCard(item = item)
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showGenerateDialog) {
        GenerateInvoiceDialog(
            amount = invoiceAmount,
            description = invoiceDescription,
            onConfirm = {
                showGenerateDialog = false
                // Generate invoice
            },
            onDismiss = { showGenerateDialog = false }
        )
    }
}

@Composable
fun AdminDossierPaymentView(
    clientName: String,
    dossierType: String,
    dossierId: String,
    dossierStatus: String,
    paymentStatus: PaymentStatus,
    onNavigateToDossier: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Dossier en cours",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                clientName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$dossierType - $dossierId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(dossierStatus)
                PaymentStatusBadge(status = paymentStatus)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onNavigateToDossier,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ouvrir le dossier")
            }
        }
    }
}

@Composable
fun PaymentHistoryItemCard(item: PaymentHistoryItem) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.clientName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    item.dossierType,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    item.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    item.amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                PaymentStatusBadge(status = item.status)
                if (item.transactionId.isNotEmpty()) {
                    Text(
                        item.transactionId,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun GenerateInvoiceDialog(
    amount: String,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Générer la facture") },
        text = {
            Column {
                Text("Montant: $amount CAD")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Description: $description")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Cette action est irréversible.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

data class PaymentHistoryItem(
    val clientName: String,
    val dossierType: String,
    val amount: String,
    val status: PaymentStatus,
    val date: String,
    val transactionId: String
)
