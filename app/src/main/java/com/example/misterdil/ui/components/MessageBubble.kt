package com.example.misterdil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

enum class MessageSender {
    ADMIN,
    CLIENT,
    SYSTEM
}

@Composable
fun MessageBubble(
    text: String,
    sender: MessageSender,
    timestamp: String,
    attachmentName: String? = null,
    avatarUrl: String? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (sender) {
        MessageSender.ADMIN -> MaterialTheme.colorScheme.secondaryContainer
        MessageSender.CLIENT -> MaterialTheme.colorScheme.primary
        MessageSender.SYSTEM -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (sender) {
        MessageSender.ADMIN -> MaterialTheme.colorScheme.onSecondaryContainer
        MessageSender.CLIENT -> MaterialTheme.colorScheme.onPrimary
        MessageSender.SYSTEM -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val alignment = when (sender) {
        MessageSender.ADMIN -> Alignment.Start
        MessageSender.CLIENT -> Alignment.End
        MessageSender.SYSTEM -> Alignment.Center
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (alignment == Alignment.Center) Arrangement.Center else if (alignment == Alignment.Start) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        // Avatar for admin messages
        if (sender == MessageSender.ADMIN && avatarUrl != null) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (sender == MessageSender.ADMIN) {
            // Fallback avatar for admin without image
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "A",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    backgroundColor,
                    RoundedCornerShape(
                        topStart = if (alignment == Alignment.End) 16.dp else 4.dp,
                        topEnd = if (alignment == Alignment.Start) 16.dp else 4.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                if (sender == MessageSender.SYSTEM) {
                    Text(
                        text,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    if (attachmentName != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                attachmentName,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
