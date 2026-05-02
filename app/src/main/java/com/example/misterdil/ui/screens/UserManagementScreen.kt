package com.example.misterdil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.misterdil.ui.components.StatusBadge

data class User(
    val id: String,
    val name: String,
    val email: String,
    val status: String, // "active", "inactive"
    val dossierCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var users by remember {
        mutableStateOf(
            listOf(
                User("user-1", "Jean Dupont", "jean.dupont@email.com", "active", 2),
                User("user-2", "Marie Martin", "marie.martin@email.com", "active", 1),
                User("user-3", "Pierre Bernard", "pierre.bernard@email.com", "inactive", 0),
                User("user-4", "Sophie Dubois", "sophie.dubois@email.com", "active", 3),
                User("user-5", "Lucas Moreau", "lucas.moreau@email.com", "active", 1)
            )
        )
    }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des utilisateurs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher un utilisateur") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredUsers = if (searchQuery.isEmpty()) {
                    users
                } else {
                    users.filter { 
                        it.name.contains(searchQuery, ignoreCase = true) || 
                        it.email.contains(searchQuery, ignoreCase = true)
                    }
                }
                
                items(filteredUsers) { user ->
                    UserCard(
                        user = user,
                        onToggleStatus = {
                            users = users.map { 
                                if (it.id == user.id) {
                                    it.copy(status = if (it.status == "active") "inactive" else "active")
                                } else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        user.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${user.dossierCount} dossier(s)",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                StatusBadge(user.status)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onToggleStatus) {
                    Icon(
                        if (user.status == "active") Icons.Default.Block else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (user.status == "active") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
