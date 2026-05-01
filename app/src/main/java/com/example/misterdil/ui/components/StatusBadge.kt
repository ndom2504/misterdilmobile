package com.example.misterdil.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (status.lowercase()) {
        "en attente" -> MaterialTheme.colorScheme.secondaryContainer to "En attente"
        "en cours" -> MaterialTheme.colorScheme.primaryContainer to "En cours"
        "soumis" -> MaterialTheme.colorScheme.tertiaryContainer to "Soumis"
        "complété" -> MaterialTheme.colorScheme.primary to "Complété"
        "bloqué" -> MaterialTheme.colorScheme.errorContainer to "Bloqué"
        else -> MaterialTheme.colorScheme.outline to status
    }

    Badge(
        containerColor = color,
        modifier = modifier
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
