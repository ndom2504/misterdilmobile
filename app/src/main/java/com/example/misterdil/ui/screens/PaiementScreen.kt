package com.example.misterdil.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.ui.viewmodels.PaymentUiState
import com.example.misterdil.ui.viewmodels.PaymentViewModel
import androidx.activity.ComponentActivity
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaiementScreen(viewModel: PaymentViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    val activity = context as ComponentActivity
    val paymentSheet = remember(activity) {
        PaymentSheet.Builder { paymentResult ->
            when (paymentResult) {
                is PaymentSheetResult.Completed -> {
                    Toast.makeText(context, "Paiement réussi !", Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is PaymentSheetResult.Canceled -> {
                    Toast.makeText(context, "Paiement annulé", Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is PaymentSheetResult.Failed -> {
                    Toast.makeText(context, "Erreur: ${paymentResult.error.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }
            }
        }.build(activity)
    }

    LaunchedEffect(uiState) {
        if (uiState is PaymentUiState.Success) {
            val response = (uiState as PaymentUiState.Success).response
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
                actions = {
                    IconButton(onClick = { /* Actions */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Plus")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    FinancialSummaryCard(onPayClick = {
                        viewModel.preparePayment(425000) // 4250.00$ en centimes
                    })
                }

                item {
                    Text(
                        text = "Historique des transactions",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(sampleInvoices) { invoice ->
                    InvoiceItem(invoice)
                }
            }

            if (uiState is PaymentUiState.Loading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialSummaryCard(onPayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Solde total à régler", style = MaterialTheme.typography.labelLarge)
            }
            Text(
                text = "4 250,00 $",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onPayClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF635BFF) // Stripe Purple
                )
            ) {
                Text("Payer avec Stripe")
            }
        }
    }
}

@Composable
fun InvoiceItem(invoice: Invoice) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invoice.clientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = invoice.projectName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = invoice.date,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${invoice.amount} $",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                InvoiceStatusBadge(invoice.status)
                IconButton(onClick = { /* Download */ }) {
                    Icon(
                        Icons.Default.Download, 
                        contentDescription = "Télécharger",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceStatusBadge(status: String) {
    val color = when (status) {
        "Payé" -> Color(0xFF4CAF50)
        "En attente" -> Color(0xFFFFA000)
        "En retard" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    Text(
        text = status,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold
    )
}

data class Invoice(
    val id: String,
    val clientName: String,
    val projectName: String,
    val amount: String,
    val date: String,
    val status: String
)

val sampleInvoices = listOf(
    Invoice("INV-001", "Jean Dupont", "Entrée Express", "1 500,00", "15 Mars 2024", "Payé"),
    Invoice("INV-002", "Marie Curie", "Permis d'études", "850,00", "10 Avril 2024", "En attente"),
    Invoice("INV-003", "InnoTech S.A.R.L", "Plan d'affaires", "2 200,00", "01 Avril 2024", "En retard"),
    Invoice("INV-004", "Ahmed Salem", "Études", "750,00", "20 Fév. 2024", "Payé")
)
