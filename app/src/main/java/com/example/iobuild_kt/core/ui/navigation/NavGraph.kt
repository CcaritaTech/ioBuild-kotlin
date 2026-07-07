package com.example.iobuild_kt.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.iobuild_kt.auth.domain.repository.AuthRepository
import com.example.iobuild_kt.auth.presentation.LoginScreen
import com.example.iobuild_kt.clients.presentation.client_list.ClientListScreen
import com.example.iobuild_kt.subscription.presentation.SubscriptionScreen
import com.example.iobuild_kt.auth.presentation.RegisterAccountScreen
import com.example.iobuild_kt.auth.presentation.RegisterProfileScreen
import com.example.iobuild_kt.auth.presentation.RegisterViewModel
import com.example.iobuild_kt.core.data.TokenManager
import com.example.iobuild_kt.core.i18n.LocalLanguage
import com.example.iobuild_kt.core.ui.components.IoScaffold
import com.example.iobuild_kt.dashboard.presentation.DashboardScreen
import com.example.iobuild_kt.devices.presentation.device_list.DeviceListScreen
import com.example.iobuild_kt.profile.presentation.ProfileScreen
import com.example.iobuild_kt.projects.presentation.project_detail.ProjectDetailScreen
import com.example.iobuild_kt.projects.presentation.project_form.ProjectFormScreen
import com.example.iobuild_kt.projects.presentation.project_list.ProjectListScreen
import com.example.iobuild_kt.subscription.presentation.SubscriptionAccessState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun NavGraph(
    navController: NavHostController,
    currentLang: String,
    onLanguageChange: (String) -> Unit,
    startDestination: String = Screen.Login.route
) {
    val authRepository: AuthRepository = koinInject()
    val tokenManager: TokenManager = koinInject()
    val subscriptionAccessState: SubscriptionAccessState = koinInject()
    val scope = rememberCoroutineScope()

    LaunchedEffect(startDestination) {
        if (startDestination == Screen.Dashboard.route) {
            val builderId = tokenManager.userId.first()
            if (builderId != null) subscriptionAccessState.refresh(builderId)
        }
    }

    // Clears the session token AND the local Room cache — otherwise the next account to log in
    // on this device would see this account's cached projects/devices/clients until the cache's
    // 5-minute TTL expired.
    val handleLogout: () -> Unit = {
        scope.launch { authRepository.signOut() }
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    CompositionLocalProvider(LocalLanguage provides currentLang) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // -- PUBLIC --
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        scope.launch {
                            val builderId = tokenManager.userId.first()
                            if (builderId != null) subscriptionAccessState.refresh(builderId)
                            val destination = if (subscriptionAccessState.hasActiveSubscription.value == true) {
                                Screen.Dashboard.route
                            } else {
                                Screen.Subscription.route
                            }
                            navController.navigate(destination) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }

            navigation(startDestination = Screen.RegisterAccount.route, route = "register") {
                composable(Screen.RegisterAccount.route) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("register") }
                    val vm = koinViewModel<RegisterViewModel>(viewModelStoreOwner = parentEntry)
                    RegisterAccountScreen(
                        viewModel = vm,
                        onNext = { navController.navigate(Screen.RegisterProfile.route) },
                        onCancel = { navController.popBackStack() }
                    )
                }
                composable(Screen.RegisterProfile.route) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("register") }
                    val vm = koinViewModel<RegisterViewModel>(viewModelStoreOwner = parentEntry)
                    RegisterProfileScreen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onRegisterSuccess = {
                            scope.launch {
                                val builderId = tokenManager.userId.first()
                                if (builderId != null) subscriptionAccessState.refresh(builderId)
                                val destination = if (subscriptionAccessState.hasActiveSubscription.value == true) {
                                    Screen.Dashboard.route
                                } else {
                                    Screen.Subscription.route
                                }
                                navController.navigate(destination) {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }

            // -- AUTHENTICATED (wrapped with IoScaffold) --

            composable(Screen.Dashboard.route) {
                GatedRoute(navController) {
                    IoScaffold(
                        currentRoute = Screen.Dashboard.route,
                        currentLang = currentLang,
                        onNavigate = { screen -> navController.navigate(screen.route) },
                        onLogout = handleLogout,
                        onLanguageChange = onLanguageChange
                    ) {
                        DashboardScreen()
                    }
                }
            }

            composable(Screen.ProjectList.route) {
                GatedRoute(navController) {
                    IoScaffold(
                        currentRoute = Screen.ProjectList.route,
                        currentLang = currentLang,
                        onNavigate = { screen -> navController.navigate(screen.route) },
                        onLogout = handleLogout,
                        onLanguageChange = onLanguageChange
                    ) {
                        ProjectListScreen(
                            onProjectClick = { id ->
                                navController.navigate(Screen.ProjectDetail.createRoute(id))
                            },
                            onCreateClick = {
                                navController.navigate(Screen.ProjectForm.route)
                            }
                        )
                    }
                }
            }

            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.IntType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getInt("projectId") ?: return@composable
                val vm = koinViewModel<com.example.iobuild_kt.projects.presentation.project_detail.ProjectDetailViewModel>()
                GatedRoute(navController) {
                    IoScaffold(
                        currentRoute = Screen.ProjectList.route,
                        currentLang = currentLang,
                        onNavigate = { screen -> navController.navigate(screen.route) },
                        onLogout = handleLogout,
                        onLanguageChange = onLanguageChange
                    ) {
                        ProjectDetailScreen(
                            projectId = projectId,
                            onBack = { navController.popBackStack() },
                            onEdit = { id ->
                                navController.navigate(Screen.ProjectForm.createRoute(id))
                            },
                            viewModel = vm
                        )
                    }
                }
            }

            composable(
                route = Screen.ProjectForm.route,
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getInt("projectId")
                val actualId = if (projectId != null && projectId > 0) projectId else null
                val vm = koinViewModel<com.example.iobuild_kt.projects.presentation.project_form.ProjectFormViewModel>()
                GatedRoute(navController) {
                    IoScaffold(
                        currentRoute = Screen.ProjectList.route,
                        currentLang = currentLang,
                        onNavigate = { screen -> navController.navigate(screen.route) },
                        onLogout = handleLogout,
                        onLanguageChange = onLanguageChange
                    ) {
                        ProjectFormScreen(
                            projectId = actualId,
                            onBack = { navController.popBackStack() },
                            viewModel = vm
                        )
                    }
                }
            }

            composable(Screen.DeviceList.route) {
                GatedRoute(navController) {
                    IoScaffold(currentRoute = Screen.DeviceList.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = handleLogout, onLanguageChange = onLanguageChange) {
                        DeviceListScreen()
                    }
                }
            }
            composable(Screen.Profile.route) {
                IoScaffold(currentRoute = Screen.Profile.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = handleLogout, onLanguageChange = onLanguageChange) {
                    ProfileScreen()
                }
            }

            composable(Screen.ClientList.route) {
                GatedRoute(navController) {
                    IoScaffold(currentRoute = Screen.ClientList.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = handleLogout, onLanguageChange = onLanguageChange) {
                        ClientListScreen()
                    }
                }
            }

            composable(Screen.Subscription.route) {
                IoScaffold(currentRoute = Screen.Subscription.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = handleLogout, onLanguageChange = onLanguageChange) {
                    SubscriptionScreen()
                }
            }

            // -- PLACEHOLDERS (settings not yet built; kept so nav drawer items don't crash) --
            composable(Screen.Settings.route) {
                IoScaffold(currentRoute = Screen.Settings.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = handleLogout, onLanguageChange = onLanguageChange) {
                    PlaceholderScreen(title = Screen.Settings.title)
                }
            }
        }
    }
}
