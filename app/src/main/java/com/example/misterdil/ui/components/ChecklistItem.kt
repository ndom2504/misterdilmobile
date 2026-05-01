package com.example.misterdil.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ChecklistStatus {
    COMPLETED,
    PENDING,
    BLOCKED
}

@Composable
fun ChecklistItem(
    label: String,
    status: ChecklistStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (status) {
        ChecklistStatus.COMPLETED -> Icons.Default.CheckCircle to MaterialTheme.colorScheme.primary
        ChecklistStatus.PENDING -> Icons.Default.Pending to MaterialTheme.colorScheme.secondary
        ChecklistStatus.BLOCKED -> Icons.Default.Error to MaterialTheme.colorScheme.error
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (status == ChecklistStatus.BLOCKED) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
