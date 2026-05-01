package com.example.misterdil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.misterdil.ui.screens.*
import com.example.misterdil.ui.theme.MIsterdilTheme
import com.example.misterdil.ui.viewmodels.*

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as MisterdilApplication).authRepository)
    }
    
    private val dossierViewModel: DossierViewModel by viewModels {
        DossierViewModelFactory((application as MisterdilApplication).repository)
    }
    
    private val paymentViewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory((application as MisterdilApplication).paymentApiService)
    }

    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory((application as MisterdilApplication).chatRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MIsterdilTheme {
                val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
                val isAdmin by authViewModel.isAdmin.collectAsState()
                var introFinished by rememberSaveable { mutableStateOf(false) }
                var showRegister by rememberSaveable { mutableStateOf(false) }

                when {
                    !introFinished -> IntroScreen(onFinished = { introFinished = true })
                    isAuthenticated -> MIsterdilApp(
                        authViewModel, dossierViewModel, paymentViewModel, chatViewModel, isAdmin
                    )
                    showRegister -> RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateToLogin = { showRegister = false }
                    )
                    else -> LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { showRegister = true }
                    )
                }
            }
        }
    }
}

@Composable
fun MIsterdilApp(
    authViewModel: AuthViewModel,
    dossierViewModel: DossierViewModel,
    paymentViewModel: PaymentViewModel,
    chatViewModel: ChatViewModel,
    isAdmin: Boolean
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val visibleDestinations = AppDestinations.entries.filter { !it.adminOnly || isAdmin }

    if (currentDestination.adminOnly && !isAdmin) {
        currentDestination = AppDestinations.HOME
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            visibleDestinations.forEach {
                item(
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.label
                        )
                    },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (currentDestination) {
                AppDestinations.HOME -> {
                    val userName by authViewModel.userName.collectAsState()
                    if (isAdmin) {
                        AdminHomeScreen(
                            userName = userName,
                            dossierViewModel = dossierViewModel,
                            chatViewModel = chatViewModel,
                            modifier = modifier,
                            onNavigateTo = { dest ->
                                currentDestination = when (dest) {
                                    "dossier"    -> AppDestinations.DOSSIER
                                    "messagerie" -> AppDestinations.MESSAGERIE
                                    "paiement"   -> AppDestinations.PAIEMENT
                                    else         -> AppDestinations.HOME
                                }
                            }
                        )
                    } else {
                        ClientHomeScreen(
                            authViewModel = authViewModel,
                            dossierViewModel = dossierViewModel,
                            userName = userName,
                            modifier = modifier,
                            onNavigateTo = { dest ->
                                currentDestination = when (dest) {
                                    "dossier"    -> AppDestinations.DOSSIER
                                    "messagerie" -> AppDestinations.MESSAGERIE
                                    "paiement"   -> AppDestinations.PAIEMENT
                                    "profil"     -> AppDestinations.PROFIL
                                    "create_dossier" -> AppDestinations.DOSSIER
                                    else         -> AppDestinations.HOME
                                }
                            }
                        )
                    }
                }
                AppDestinations.DOSSIER -> {
                    val isAdmin by authViewModel.isAdmin.collectAsState()
                    DossierScreen(dossierViewModel, chatViewModel, modifier, isAdmin)
                }
                AppDestinations.MESSAGERIE -> {
                    val isAdmin by authViewModel.isAdmin.collectAsState()
                    if (isAdmin) {
                        AdminMessagingScreen(
                            viewModel = chatViewModel,
                            onNavigateToDossier = { dest ->
                                currentDestination = when {
                                    dest.startsWith("dossier") -> AppDestinations.DOSSIER
                                    else -> AppDestinations.HOME
                                }
                            },
                            modifier = modifier
                        )
                    } else {
                        ClientMessagingScreen(
                            viewModel = chatViewModel,
                            onNavigateToDossier = { dest ->
                                currentDestination = when {
                                    dest.startsWith("dossier") -> AppDestinations.DOSSIER
                                    else -> AppDestinations.HOME
                                }
                            },
                            modifier = modifier
                        )
                    }
                }
                AppDestinations.PAIEMENT -> {
                    val isAdmin by authViewModel.isAdmin.collectAsState()
                    if (isAdmin) {
                        AdminPaymentScreen(
                            viewModel = paymentViewModel,
                            onBack = { currentDestination = AppDestinations.HOME },
                            onNavigateToDossier = { currentDestination = AppDestinations.DOSSIER },
                            modifier = modifier
                        )
                    } else {
                        ClientPaymentScreen(
                            viewModel = paymentViewModel,
                            onBack = { currentDestination = AppDestinations.HOME },
                            onNavigateToDossier = { currentDestination = AppDestinations.DOSSIER },
                            modifier = modifier
                        )
                    }
                }
                AppDestinations.PROFIL -> ProfilScreen(authViewModel, modifier)
                AppDestinations.ADMIN -> AdminScreen(authViewModel, chatViewModel, modifier)
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val adminOnly: Boolean = false
) {
    HOME("Accueil", Icons.Default.Home),
    DOSSIER("Dossiers", Icons.AutoMirrored.Filled.List),
    MESSAGERIE("Messagerie", Icons.Default.Email),
    PAIEMENT("Paiement", Icons.Default.ShoppingCart),
    PROFIL("Profil", Icons.Default.Person),
    ADMIN("Admin", Icons.Default.AdminPanelSettings, adminOnly = true),
}
