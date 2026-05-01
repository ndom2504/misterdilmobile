package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var paymentNotificationsEnabled by remember { mutableStateOf(true) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Jean Dupont") }
    var userPhone by remember { mutableStateOf("+33 6 12 34 56 78") }
    var userLanguage by remember { mutableStateOf("Français") }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier
    ) { padding ->
        if (isTablet) {
            // Tablet layout: split view
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProfileSection(title = "Informations personnelles") {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        userName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "client@example.com",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileInfoRow(label = "Téléphone", value = userPhone)
                            Spacer(modifier = Modifier.height(8.dp))
                            ProfileInfoRow(label = "Langue", value = userLanguage)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Modifier mes informations")
                            }
                        }
                    }
                    SecurityCard(
                        title = "Sécurité du compte",
                        items = listOf(
                            SecurityItem("Mot de passe", "Changé il y a 30 jours"),
                            SecurityItem("Connexion sécurisée", "Activée"),
                            SecurityItem("Dernière connexion", "Aujourd'hui à 10:30")
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showChangePasswordDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Changer mot de passe")
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sécurité renforcée")
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProfileSection(title = "Préférences") {
                        PreferenceToggle(
                            label = "Notifications de nouveaux messages",
                            checked = notificationsEnabled,
                            onCheckedChange = { 
                                notificationsEnabled = it
                                authViewModel.updateNotifications(it, paymentNotificationsEnabled)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PreferenceToggle(
                            label = "Notifications d'actions requises",
                            checked = paymentNotificationsEnabled,
                            onCheckedChange = { 
                                paymentNotificationsEnabled = it
                                authViewModel.updateNotifications(notificationsEnabled, it)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PreferenceToggle(
                            label = "Notifications de paiements",
                            checked = paymentNotificationsEnabled,
                            onCheckedChange = { 
                                paymentNotificationsEnabled = it
                                authViewModel.updateNotifications(notificationsEnabled, it)
                            }
                        )
                    }
                    ProfileSection(title = "Documents & historique") {
                        ProfileActionItem(
                            label = "Mes dossiers (archives)",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Reçus de paiement",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Documents légaux",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                    ProfileSection(title = "Aide & support") {
                        ProfileActionItem(
                            label = "Centre d'aide / FAQ",
                            icon = Icons.Default.Help,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Contact support",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Mentions légales",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Politique de confidentialité",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                    OutlinedButton(
                        onClick = { showDeleteAccountDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Demander la suppression de compte")
                    }
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Déconnexion")
                    }
                }
            }
        } else {
            // Mobile layout
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Carte identité
                item {
                    ProfileSection(title = "Informations personnelles") {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        userName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "client@example.com",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileInfoRow(label = "Téléphone", value = userPhone)
                            Spacer(modifier = Modifier.height(8.dp))
                            ProfileInfoRow(label = "Langue", value = userLanguage)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Modifier mes informations")
                            }
                        }
                    }
                }

                // Sécurité du compte
                item {
                    SecurityCard(
                        title = "Sécurité du compte",
                        items = listOf(
                            SecurityItem("Mot de passe", "Changé il y a 30 jours"),
                            SecurityItem("Connexion sécurisée", "Activée"),
                            SecurityItem("Dernière connexion", "Aujourd'hui à 10:30")
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showChangePasswordDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Changer mot de passe")
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sécurité renforcée")
                        }
                    }
                }

                // Préférences
                item {
                    ProfileSection(title = "Préférences") {
                        PreferenceToggle(
                            label = "Notifications de nouveaux messages",
                            checked = notificationsEnabled,
                            onCheckedChange = { 
                                notificationsEnabled = it
                                authViewModel.updateNotifications(it, paymentNotificationsEnabled)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PreferenceToggle(
                            label = "Notifications d'actions requises",
                            checked = paymentNotificationsEnabled,
                            onCheckedChange = { 
                                paymentNotificationsEnabled = it
                                authViewModel.updateNotifications(notificationsEnabled, it)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PreferenceToggle(
                            label = "Notifications de paiements",
                            checked = paymentNotificationsEnabled,
                            onCheckedChange = { 
                                paymentNotificationsEnabled = it
                                authViewModel.updateNotifications(notificationsEnabled, it)
                            }
                        )
                    }
                }

                // Documents & historique
                item {
                    ProfileSection(title = "Documents & historique") {
                        ProfileActionItem(
                            label = "Mes dossiers (archives)",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Reçus de paiement",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Documents légaux",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                }

                // Aide & support
                item {
                    ProfileSection(title = "Aide & support") {
                        ProfileActionItem(
                            label = "Centre d'aide / FAQ",
                            icon = Icons.Default.Help,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Contact support",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Mentions légales",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Politique de confidentialité",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                }

                // Actions secondaires
                item {
                    OutlinedButton(
                        onClick = { showDeleteAccountDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Demander la suppression de compte")
                    }
                }

                item {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Déconnexion")
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Dialogs
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = userName,
            currentEmail = "client@example.com",
            currentPhone = userPhone,
            currentLanguage = userLanguage,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, phone, language ->
                authViewModel.updateProfile(name, phone, language,
                    onSuccess = {
                        userName = name
                        userPhone = phone
                        userLanguage = language
                        showEditProfileDialog = false
                    },
                    onError = { error ->
                        // Show error
                    }
                )
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onChangePassword = { currentPassword, newPassword ->
                authViewModel.changePassword(currentPassword, newPassword,
                    onSuccess = {
                        showChangePasswordDialog = false
                    },
                    onError = { error ->
                        // Show error in dialog
                    }
                )
            }
        )
    }

    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteAccountDialog = false },
            onDeleteAccount = {
                authViewModel.deleteAccount(
                    onSuccess = {
                        showDeleteAccountDialog = false
                        onLogout()
                    },
                    onError = { error ->
                        // Show error
                    }
                )
            }
        )
    }
}
