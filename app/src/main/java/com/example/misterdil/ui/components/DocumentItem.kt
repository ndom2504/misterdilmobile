package com.example.misterdil.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class DocumentStatus {
    NOT_PROVIDED,
    PENDING_VALIDATION,
    VALIDATED,
    REJECTED
}

@Composable
fun DocumentItem(
    label: String,
    status: DocumentStatus,
    rejectionReason: String? = null,
    onUpload: () -> Unit = {},
    modifier: Modifier = Modifier,
    showActions: Boolean = true
) {
    val icon = when (status) {
        DocumentStatus.NOT_PROVIDED -> Icons.Default.Upload
        DocumentStatus.PENDING_VALIDATION -> Icons.Default.Warning
        DocumentStatus.VALIDATED -> Icons.Default.CheckCircle
        DocumentStatus.REJECTED -> Icons.Default.Error
    }
    val iconColor = when (status) {
        DocumentStatus.NOT_PROVIDED -> MaterialTheme.colorScheme.secondary
        DocumentStatus.PENDING_VALIDATION -> MaterialTheme.colorScheme.tertiary
        DocumentStatus.VALIDATED -> MaterialTheme.colorScheme.primary
        DocumentStatus.REJECTED -> MaterialTheme.colorScheme.error
    }
    val bgColor = when (status) {
        DocumentStatus.NOT_PROVIDED -> MaterialTheme.colorScheme.secondaryContainer
        DocumentStatus.PENDING_VALIDATION -> MaterialTheme.colorScheme.tertiaryContainer
        DocumentStatus.VALIDATED -> MaterialTheme.colorScheme.primaryContainer
        DocumentStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
    }
    val statusText = when (status) {
        DocumentStatus.NOT_PROVIDED -> "Non fourni"
        DocumentStatus.PENDING_VALIDATION -> "En attente"
        DocumentStatus.VALIDATED -> "Validé"
        DocumentStatus.REJECTED -> "Rejeté"
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = bgColor
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = iconColor
                )
                if (status == DocumentStatus.REJECTED && rejectionReason != null) {
                    Text(
                        rejectionReason,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (showActions && status != DocumentStatus.VALIDATED) {
                AssistChip(
                    onClick = onUpload,
                    label = { Text(if (status == DocumentStatus.NOT_PROVIDED) "Uploader" else "Reuploader", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}
