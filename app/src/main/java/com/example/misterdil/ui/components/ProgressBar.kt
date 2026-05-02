package com.example.misterdil.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DossierProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    // Normalisation pour éviter le bug du 2000% (si la valeur reçue est 20.0 au lieu de 0.2)
    val normalizedProgress = if (progress > 1f) progress / 100f else progress
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Progression",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                "${(normalizedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { normalizedProgress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = when {
                normalizedProgress >= 1.0f -> MaterialTheme.colorScheme.primary
                normalizedProgress >= 0.5f -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.secondary
            }
        )
    }
}
