package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class ActiveSession(
    val id: String,
    val device: String,
    val location: String,
    val lastActive: String,
    val isCurrent: Boolean = false
)

@Composable
fun ActiveSessionsDialog(
    onDismiss: () -> Unit,
    onRevokeSession: (sessionId: String) -> Unit
) {
    val sessions = remember {
        listOf(
            ActiveSession("sess-1", "Chrome / Windows", "Paris, France", "Aujourd'hui à 10:30", true),
            ActiveSession("sess-2", "Safari / iPhone", "Lyon, France", "Hier à 18:45", false),
            ActiveSession("sess-3", "Firefox / Mac", "Marseille, France", "15 avril 2024", false)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        "Sessions actives (3)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sessions) { session ->
                        SessionCard(
                            session = session,
                            onRevoke = if (!session.isCurrent) { { onRevokeSession(session.id) } } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCard(
    session: ActiveSession,
    onRevoke: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (session.isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    session.device,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    session.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (session.isCurrent) "Session actuelle" else session.lastActive,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (session.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
            if (onRevoke != null) {
                OutlinedButton(
                    onClick = onRevoke,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Révoquer")
                }
            }
        }
    }
}
