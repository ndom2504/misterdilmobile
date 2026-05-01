package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
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
fun ClientPaymentScreen(
    viewModel: PaymentViewModel,
    onBack: () -> Unit,
    onNavigateToDossier: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var paymentStatus by remember { mutableStateOf(PaymentStatus.PENDING) }
    var showConfirmation by remember { mutableStateOf(false) }
    var transactionId by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

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
                        DossierPaymentHeader(
                            dossierType = "Permis d'études",
                            dossierId = "DOS-2024-001",
                            dossierStatus = "En cours"
                        )
                        PaymentSummaryCard(
                            dossierType = "Permis d'études",
                            dossierId = "DOS-2024-001",
                            description = "Frais de traitement du dossier",
                            amount = "500 €",
                            dueDate = "30 avril 2024"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PaymentStatusBadge(status = paymentStatus)
                        }
                        StripeButton(
                            amount = "500 €",
                            enabled = paymentStatus == PaymentStatus.PENDING,
                            onClick = {
                                paymentStatus = PaymentStatus.PAID
                                transactionId = "TXN-" + System.currentTimeMillis().toString().takeLast(8)
                                showConfirmation = true
                            }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Paiement sécurisé",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Vos informations de paiement sont traitées de manière sécurisée par Stripe. Nous ne stockons aucune donnée de carte bancaire.",
                                    style = MaterialTheme.typography.bodySmall
                                )
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
                        DossierPaymentHeader(
                            dossierType = "Permis d'études",
                            dossierId = "DOS-2024-001",
                            dossierStatus = "En cours"
                        )
                    }

                    item {
                        PaymentSummaryCard(
                            dossierType = "Permis d'études",
                            dossierId = "DOS-2024-001",
                            description = "Frais de traitement du dossier",
                            amount = "500 €",
                            dueDate = "30 avril 2024"
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PaymentStatusBadge(status = paymentStatus)
                        }
                    }

                    item {
                        StripeButton(
                            amount = "500 €",
                            enabled = paymentStatus == PaymentStatus.PENDING,
                            onClick = {
                                paymentStatus = PaymentStatus.PAID
                                transactionId = "TXN-" + System.currentTimeMillis().toString().takeLast(8)
                                showConfirmation = true
                            }
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Paiement sécurisé",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Vos informations de paiement sont traitées de manière sécurisée par Stripe. Nous ne stockons aucune donnée de carte bancaire.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
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
