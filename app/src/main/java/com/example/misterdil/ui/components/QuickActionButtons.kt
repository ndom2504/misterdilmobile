package com.example.misterdil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminQuickActions(
    onRequestDocument: () -> Unit = {},
    onRequestCorrection: () -> Unit = {},
    onValidateStep: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onRequestDocument,
            label = { Text("📄 Demander document", style = MaterialTheme.typography.labelSmall) },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = onRequestCorrection,
            label = { Text("✏️ Demander correction", style = MaterialTheme.typography.labelSmall) },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = onValidateStep,
            label = { Text("✅ Valider étape", style = MaterialTheme.typography.labelSmall) },
            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) },
            modifier = Modifier.weight(1f)
        )
    }
}
