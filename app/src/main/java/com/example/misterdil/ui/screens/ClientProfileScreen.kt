package com.example.misterdil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
fun ClientProfileScreen(
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
    
    var showEditProfile by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (showEditProfile) {
        ProfileScreen(
            repository = dossierRepository,
            authRepository = authRepository,
            currentName = userName ?: "",
            currentAvatarUrl = photoUri,
            userId = userId ?: "client",
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
                title = { Text("Mon Profil", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Carte identité Client
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
                                        (userName ?: "U").firstOrNull()?.uppercaseChar()?.toString() ?: "U",
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
                                StatusBadge("Client")
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
                ProfileSection(title = "Sécurité") {
                    ProfileActionItem(label = "Changer mot de passe", icon = Icons.Default.Lock, onClick = { showChangePasswordDialog = true })
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
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onChangePassword = { current, new ->
                authViewModel.changePassword(current, new, onSuccess = { showChangePasswordDialog = false })
            }
        )
    }

    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteAccountDialog = false },
            onDeleteAccount = { authViewModel.deleteAccount(onSuccess = { onLogout() }) }
        )
    }
}
