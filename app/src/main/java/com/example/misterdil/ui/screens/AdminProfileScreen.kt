package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.misterdil.data.repository.AuthRepository
import com.example.misterdil.data.repository.DossierRepository
import com.example.misterdil.ui.components.*
import com.example.misterdil.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    authViewModel: AuthViewModel,
    dossierRepository: DossierRepository,
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by authViewModel.userName.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()
    val photoUri by authViewModel.photoUri.collectAsState()
    val userId by authViewModel.userId.collectAsState()
    
    var showSessionsDialog by remember { mutableStateOf(false) }
    var showDossierTypesDialog by remember { mutableStateOf(false) }
    var showUserManagement by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }
    
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (showEditProfile) {
        ProfileScreen(
            repository = dossierRepository,
            authRepository = authRepository,
            currentName = userName ?: "",
            currentAvatarUrl = photoUri,
            userId = userId ?: "admin",
            onBack = { showEditProfile = false },
            onSaveSuccess = { newName, newAvatar ->
                authViewModel.updateNameLocally(newName)
                newAvatar?.let { authViewModel.updatePhotoUri(it) }
            },
            modifier = modifier
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Admin", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
                                if (photoUri != null) {
                                    AsyncImage(
                                        model = photoUri,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        (userName ?: "A").first().uppercaseChar().toString(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(userName ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(userEmail ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                StatusBadge("Admin")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { showEditProfile = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modifier photo et nom")
                        }
                    }
                }
            }

            item {
                ProfileSection(title = "Gestion") {
                    ProfileActionItem(label = "Liste des clients", icon = Icons.Default.SupervisorAccount, onClick = { showUserManagement = true })
                    Spacer(Modifier.height(8.dp))
                    ProfileActionItem(label = "Types de dossiers", icon = Icons.Default.Settings, onClick = { showDossierTypesDialog = true })
                }
            }

            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Déconnexion")
                }
            }
        }
    }

    if (showDossierTypesDialog) {
        DossierTypesDialog(onDismiss = { showDossierTypesDialog = false }, onSave = {})
    }

    if (showUserManagement) {
        UserManagementScreen(onBack = { showUserManagement = false })
    }
}
