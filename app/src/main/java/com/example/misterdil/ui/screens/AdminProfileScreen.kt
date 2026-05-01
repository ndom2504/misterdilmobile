package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
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
fun AdminProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSessionsDialog by remember { mutableStateOf(false) }
    var showDossierTypesDialog by remember { mutableStateOf(false) }
    var showUserManagement by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Admin", fontWeight = FontWeight.Bold) }
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.SupervisorAccount,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Admin Principal",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "admin@misterdil.com",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusBadge("Admin")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileInfoRow(label = "Rôle", value = "Super Admin")
                            Spacer(modifier = Modifier.height(8.dp))
                            ProfileInfoRow(label = "Dernière connexion", value = "Aujourd'hui à 09:15")
                        }
                    }
                    ProfileSection(title = "Paramètres de sécurité") {
                        ProfileActionItem(
                            label = "Changer mot de passe",
                            icon = Icons.Default.Lock,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Sessions actives (3)",
                            icon = Icons.Default.Person,
                            onClick = { showSessionsDialog = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Clé API",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Journal d'accès",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                    ProfileSection(title = "Paramètres globaux") {
                        ProfileActionItem(
                            label = "Gestion des types de dossiers",
                            icon = Icons.Default.Settings,
                            onClick = { showDossierTypesDialog = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Paramètres de facturation",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Modèles de messages",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Configuration Stripe (lecture seule)",
                            icon = Icons.Default.Lock,
                            onClick = {}
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProfileSection(title = "Gestion des utilisateurs") {
                        ProfileActionItem(
                            label = "Liste des clients",
                            icon = Icons.Default.SupervisorAccount,
                            onClick = { showUserManagement = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Désactivation / Réactivation",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Consultation profils clients",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                    ProfileSection(title = "Monitoring & système") {
                        ProfileActionItem(
                            label = "État backend / base de données",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Logs récents",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Console Neon",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Console Vercel",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
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
                // Carte identité Admin
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.SupervisorAccount,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Admin Principal",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "admin@misterdil.com",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusBadge("Admin")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileInfoRow(label = "Rôle", value = "Super Admin")
                            Spacer(modifier = Modifier.height(8.dp))
                            ProfileInfoRow(label = "Dernière connexion", value = "Aujourd'hui à 09:15")
                        }
                    }
                }

                // Paramètres de sécurité
                item {
                    ProfileSection(title = "Paramètres de sécurité") {
                        ProfileActionItem(
                            label = "Changer mot de passe",
                            icon = Icons.Default.Lock,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Sessions actives (3)",
                            icon = Icons.Default.Person,
                            onClick = { showSessionsDialog = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Clé API",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Journal d'accès",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                }

                // Paramètres globaux (Admin uniquement)
                item {
                    ProfileSection(title = "Paramètres globaux") {
                        ProfileActionItem(
                            label = "Gestion des types de dossiers",
                            icon = Icons.Default.Settings,
                            onClick = { showDossierTypesDialog = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Paramètres de facturation",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Modèles de messages",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Configuration Stripe (lecture seule)",
                            icon = Icons.Default.Lock,
                            onClick = {}
                        )
                    }
                }

                // Gestion des utilisateurs
                item {
                    ProfileSection(title = "Gestion des utilisateurs") {
                        ProfileActionItem(
                            label = "Liste des clients",
                            icon = Icons.Default.SupervisorAccount,
                            onClick = { showUserManagement = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Désactivation / Réactivation",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Consultation profils clients",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                    }
                }

                // Monitoring & système
                item {
                    ProfileSection(title = "Monitoring & système") {
                        ProfileActionItem(
                            label = "État backend / base de données",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Logs récents",
                            icon = Icons.Default.Person,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Console Neon",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileActionItem(
                            label = "Console Vercel",
                            icon = Icons.Default.Settings,
                            onClick = {}
                        )
                    }
                }

                // Actions critiques
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

    if (showSessionsDialog) {
        ActiveSessionsDialog(
            onDismiss = { showSessionsDialog = false },
            onRevokeSession = { sessionId ->
                // TODO: Implement revoke session via backend
            }
        )
    }

    if (showDossierTypesDialog) {
        DossierTypesDialog(
            onDismiss = { showDossierTypesDialog = false },
            onSave = { types ->
                // TODO: Implement save dossier types via backend
            }
        )
    }

    if (showUserManagement) {
        UserManagementScreen(
            onBack = { showUserManagement = false }
        )
    }
}
