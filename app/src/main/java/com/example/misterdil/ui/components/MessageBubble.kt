package com.example.misterdil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.InsertDriveFile
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

const val FILE_MSG_PREFIX = "__FILE__:"
const val PAYMENT_MSG_PREFIX = "__PAYMENT__:"

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
    onPay: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Handle file messages
    if (text.startsWith(FILE_MSG_PREFIX)) {
        val fileName = text.removePrefix(FILE_MSG_PREFIX)
        FileMessageBubble(fileName = fileName, sender = sender, avatarUrl = avatarUrl, modifier = modifier)
        return
    }
    // Handle payment messages
    if (text.startsWith(PAYMENT_MSG_PREFIX)) {
        val raw = text.removePrefix(PAYMENT_MSG_PREFIX)
        val parts = raw.split(":")
        val amount = parts.getOrNull(0) ?: "0"
        val desc = parts.getOrNull(1) ?: "Frais de service"
        PaymentRequestBubble(amount = amount, description = desc, sender = sender, onPay = onPay, avatarUrl = avatarUrl, modifier = modifier)
        return
    }

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

@Composable
private fun FileMessageBubble(fileName: String, sender: MessageSender, avatarUrl: String?, modifier: Modifier = Modifier) {
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
        if (sender == MessageSender.ADMIN) {
            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.InsertDriveFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(fileName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }

        if (sender == MessageSender.CLIENT) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("M", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PaymentRequestBubble(amount: String, description: String, sender: MessageSender, onPay: (() -> Unit)?, avatarUrl: String?, modifier: Modifier = Modifier) {
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
        if (sender == MessageSender.ADMIN) {
            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        ElevatedCard(
            modifier = Modifier.widthIn(max = 260.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Demande de paiement", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$$amount CAD", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(description, style = MaterialTheme.typography.bodySmall)
                if (sender == MessageSender.ADMIN && onPay != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onPay, modifier = Modifier.fillMaxWidth()) {
                        Text("Payer maintenant")
                    }
                }
            }
        }

        if (sender == MessageSender.CLIENT) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("M", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
