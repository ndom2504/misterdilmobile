package com.example.misterdil.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.misterdil.ui.viewmodels.PaymentUiState
import com.example.misterdil.ui.viewmodels.PaymentViewModel
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                transactionId = "MISTER-CAD-${System.currentTimeMillis()}"
                showConfirmation = true
                viewModel.resetState()
            }
            is PaymentSheetResult.Canceled -> {
                viewModel.resetState()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(context, "Erreur: ${result.error.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    LaunchedEffect(uiState) {
        val currentState = uiState // Fix smart cast error
        if (currentState is PaymentUiState.Success) {
            val response = currentState.response
            paymentSheet.presentWithPaymentIntent(
                response.clientSecret,
                PaymentSheet.Configuration(
                    merchantDisplayName = "Misterdil",
                    customer = response.customerId?.let {
                        PaymentSheet.CustomerConfiguration(it, response.ephemeralKeySecret!!)
                    }
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paiements & Facturation", fontWeight = FontWeight.Bold) },
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
                onDownloadReceipt = { Toast.makeText(context, "Téléchargement du reçu CAD...", Toast.LENGTH_SHORT).show() },
                onBackToDossier = onNavigateToDossier,
                modifier = Modifier.fillMaxSize().padding(padding)
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
                        Icons.Default.Payment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "Aucun paiement immédiat requis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Gérez vos transactions en toute sécurité en CAD.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Provision de dossier", style = MaterialTheme.typography.labelMedium)
                            Text("250 CAD", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.preparePayment(250, "cad") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Payer maintenant")
                            }
                        }
                    }

                    OutlinedButton(onClick = onNavigateToDossier, modifier = Modifier.fillMaxWidth()) {
                        Text("Consulter mon dossier")
                    }
                }
            }
            
            if (uiState is PaymentUiState.Loading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.4f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
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
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Paiement confirmé !",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            "Nous avons bien reçu votre règlement en CAD.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Identifiant de transaction", style = MaterialTheme.typography.labelSmall)
                Text(transactionId, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onBackToDossier, modifier = Modifier.fillMaxWidth()) {
            Text("Terminer")
        }
        
        TextButton(onClick = onDownloadReceipt) {
            Icon(Icons.Default.Download, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Télécharger le reçu (PDF)")
        }
    }
}
