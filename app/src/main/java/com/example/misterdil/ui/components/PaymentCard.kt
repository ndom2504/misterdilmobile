package com.example.misterdil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PaymentCard(
    amount: String,
    status: PaymentStatus,
    onPay: () -> Unit = {},
    onViewDetails: () -> Unit = {},
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false
) {
    val statusText = when (status) {
        PaymentStatus.PENDING -> "En attente"
        PaymentStatus.PAID -> "Payé"
        PaymentStatus.FAILED -> "Échoué"
        PaymentStatus.NOT_GENERATED -> "Non généré"
    }
    val statusColor = when (status) {
        PaymentStatus.PENDING -> MaterialTheme.colorScheme.error
        PaymentStatus.PAID -> MaterialTheme.colorScheme.primary
        PaymentStatus.FAILED -> MaterialTheme.colorScheme.error
        PaymentStatus.NOT_GENERATED -> MaterialTheme.colorScheme.secondary
    }
    val showPayButton = when (status) {
        PaymentStatus.PENDING -> true
        PaymentStatus.PAID -> false
        PaymentStatus.FAILED -> true
        PaymentStatus.NOT_GENERATED -> false
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (status == PaymentStatus.PAID)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isAdmin) Icons.Default.CreditCard else Icons.Default.Payment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
            }
            if (showPayButton && !isAdmin) {
                Button(
                    onClick = onPay,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Payer", style = MaterialTheme.typography.labelSmall)
                }
            } else if (isAdmin) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Détails", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
