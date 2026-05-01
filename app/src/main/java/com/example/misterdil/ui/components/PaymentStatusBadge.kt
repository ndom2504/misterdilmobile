package com.example.misterdil.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    NOT_GENERATED
}

@Composable
fun PaymentStatusBadge(
    status: PaymentStatus,
    modifier: Modifier = Modifier
) {
    val icon = when (status) {
        PaymentStatus.PENDING -> Icons.Default.Pending
        PaymentStatus.PAID -> Icons.Default.CheckCircle
        PaymentStatus.FAILED -> Icons.Default.Warning
        PaymentStatus.NOT_GENERATED -> Icons.Default.Pending
    }
    val text = when (status) {
        PaymentStatus.PENDING -> "En attente"
        PaymentStatus.PAID -> "Payé"
        PaymentStatus.FAILED -> "Échoué"
        PaymentStatus.NOT_GENERATED -> "Non généré"
    }
    val color = when (status) {
        PaymentStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
        PaymentStatus.PAID -> MaterialTheme.colorScheme.onPrimaryContainer
        PaymentStatus.FAILED -> MaterialTheme.colorScheme.onError
        PaymentStatus.NOT_GENERATED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val bgColor = when (status) {
        PaymentStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
        PaymentStatus.PAID -> MaterialTheme.colorScheme.primaryContainer
        PaymentStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
        PaymentStatus.NOT_GENERATED -> MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}
