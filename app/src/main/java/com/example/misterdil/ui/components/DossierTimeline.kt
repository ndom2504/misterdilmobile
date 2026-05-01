package com.example.misterdil.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class TimelineStepStatus {
    COMPLETED,
    IN_PROGRESS,
    LOCKED,
    PENDING
}

data class TimelineStep(
    val label: String,
    val status: TimelineStepStatus
)

@Composable
fun DossierTimeline(
    steps: List<TimelineStep>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        steps.forEachIndexed { index, step ->
            TimelineStepItem(
                step = step,
                isLast = index == steps.size - 1
            )
        }
    }
}

@Composable
fun TimelineStepItem(
    step: TimelineStep,
    isLast: Boolean
) {
    val icon = when (step.status) {
        TimelineStepStatus.COMPLETED -> Icons.Default.CheckCircle
        TimelineStepStatus.IN_PROGRESS -> Icons.Default.Pending
        TimelineStepStatus.LOCKED -> Icons.Default.Lock
        TimelineStepStatus.PENDING -> Icons.Default.Pending
    }
    val iconColor = when (step.status) {
        TimelineStepStatus.COMPLETED -> Color.White
        TimelineStepStatus.IN_PROGRESS -> Color.White
        TimelineStepStatus.LOCKED -> MaterialTheme.colorScheme.secondary
        TimelineStepStatus.PENDING -> MaterialTheme.colorScheme.secondary
    }
    val bgColor = when (step.status) {
        TimelineStepStatus.COMPLETED -> MaterialTheme.colorScheme.primary
        TimelineStepStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
        TimelineStepStatus.LOCKED -> MaterialTheme.colorScheme.surfaceVariant
        TimelineStepStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(
                        if (step.status == TimelineStepStatus.COMPLETED || step.status == TimelineStepStatus.IN_PROGRESS)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            step.label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (step.status == TimelineStepStatus.LOCKED)
                MaterialTheme.colorScheme.secondary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}
