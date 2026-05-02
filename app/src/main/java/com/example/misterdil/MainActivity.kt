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
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Group
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
import com.example.misterdil.data.repository.AuthRepository

class MainActivity : ComponentActivity() {
    private lateinit var authRepository: AuthRepository
    
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
        ChatViewModelFactory(
            (application as MisterdilApplication).chatRepository,
            (application as MisterdilApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authRepository = (application as MisterdilApplication).authRepository
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
    val visibleDestinations = AppDestinations.entries.filter {
        (!it.adminOnly || isAdmin) && (!it.clientOnly || !isAdmin)
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
                                currentDestination = when {
                                    dest.startsWith("dossier") -> {
                                        val id = dest.substringAfter("/", "")
                                        if (id.isNotEmpty()) dossierViewModel.navigateToDossier(id)
                                        AppDestinations.DOSSIER
                                    }
                                    dest.startsWith("messagerie") -> AppDestinations.MESSAGERIE
                                    dest.startsWith("paiement") -> AppDestinations.PAIEMENT
                                    dest.startsWith("profil") -> AppDestinations.PROFIL
                                    else -> AppDestinations.HOME
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
                                currentDestination = when {
                                    dest.startsWith("dossier") -> {
                                        val id = dest.substringAfter("/", "")
                                        if (id.isNotEmpty()) dossierViewModel.navigateToDossier(id)
                                        AppDestinations.DOSSIER
                                    }
                                    dest.startsWith("messagerie") -> AppDestinations.MESSAGERIE
                                    dest.startsWith("paiement") -> AppDestinations.PAIEMENT
                                    dest.startsWith("profil") -> AppDestinations.PROFIL
                                    dest == "create_dossier" -> AppDestinations.DOSSIER
                                    else -> AppDestinations.HOME
                                }
                            }
                        )
                    }
                }
                AppDestinations.DOSSIER -> {
                    val isAdminByAuth by authViewModel.isAdmin.collectAsState()
                    DossierScreen(dossierViewModel, chatViewModel, modifier, isAdminByAuth)
                }
                AppDestinations.MESSAGERIE -> {
                    if (isAdmin) {
                        AdminMessagingScreen(
                            viewModel = chatViewModel,
                            onNavigateToDossier = { id ->
                                dossierViewModel.navigateToDossier(id)
                                currentDestination = AppDestinations.DOSSIER
                            },
                            modifier = modifier
                        )
                    } else {
                        ClientMessagingScreen(
                            viewModel = chatViewModel,
                            onNavigateToDossier = { id ->
                                dossierViewModel.navigateToDossier(id)
                                currentDestination = AppDestinations.DOSSIER
                            },
                            onNavigateToPaiement = {
                                currentDestination = AppDestinations.PAIEMENT
                            },
                            modifier = modifier
                        )
                    }
                }
                AppDestinations.PAIEMENT -> {
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
                AppDestinations.CONSEILLER -> {
                    AdvisorScreen(
                        viewModel = dossierViewModel,
                        modifier = modifier
                    )
                }
                AppDestinations.PROFIL -> {
                    if (isAdmin) {
                        AdminProfileScreen(
                            authViewModel = authViewModel,
                            dossierRepository = dossierViewModel.repository,
                            authRepository = authRepository,
                            onLogout = { authViewModel.logout() },
                            modifier = modifier
                        )
                    } else {
                        ClientProfileScreen(
                            authViewModel = authViewModel,
                            dossierRepository = dossierViewModel.repository,
                            authRepository = authRepository,
                            onLogout = { authViewModel.logout() },
                            modifier = modifier
                        )
                    }
                }
                AppDestinations.ADMIN -> AdminScreen(authViewModel, chatViewModel, authRepository, modifier)
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val adminOnly: Boolean = false,
    val clientOnly: Boolean = false
) {
    HOME("Accueil", Icons.Default.Home),
    CONSEILLER("Conseiller", Icons.Default.Group, clientOnly = true),
    DOSSIER("Dossiers", Icons.AutoMirrored.Filled.List),
    MESSAGERIE("Messagerie", Icons.Default.Email),
    PAIEMENT("Paiement", Icons.Default.ShoppingCart),
    PROFIL("Profil", Icons.Default.Person),
    ADMIN("Admin", Icons.Default.AdminPanelSettings, adminOnly = true),
}
