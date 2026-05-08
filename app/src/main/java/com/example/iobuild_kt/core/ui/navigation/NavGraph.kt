package com.example.iobuild_kt.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.iobuild_kt.auth.presentation.LoginScreen
import com.example.iobuild_kt.core.i18n.LocalLanguage
import com.example.iobuild_kt.core.ui.components.IoScaffold
import com.example.iobuild_kt.clients.presentation.client_list.ClientListScreen
import com.example.iobuild_kt.dashboard.presentation.DashboardScreen
import com.example.iobuild_kt.devices.presentation.device_list.DeviceListScreen
import com.example.iobuild_kt.profile.presentation.ProfileScreen
import com.example.iobuild_kt.settings.presentation.SettingsScreen
import com.example.iobuild_kt.subscription.presentation.SubscriptionScreen
import com.example.iobuild_kt.projects.presentation.project_detail.ProjectDetailScreen
import com.example.iobuild_kt.projects.presentation.project_form.ProjectFormScreen
import com.example.iobuild_kt.projects.presentation.project_list.ProjectListScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    currentLang: String,
    onLanguageChange: (String) -> Unit,
    startDestination: String = Screen.Login.route
) {
    CompositionLocalProvider(LocalLanguage provides currentLang) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // -- PUBLIC --
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // -- AUTHENTICATED (wrapped with IoScaffold) --

            composable(Screen.Dashboard.route) {
                IoScaffold(
                    currentRoute = Screen.Dashboard.route,
                    currentLang = currentLang,
                    onNavigate = { screen -> navController.navigate(screen.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onLanguageChange = onLanguageChange
                ) {
                    DashboardScreen()
                }
            }

            composable(Screen.ProjectList.route) {
                IoScaffold(
                    currentRoute = Screen.ProjectList.route,
                    currentLang = currentLang,
                    onNavigate = { screen -> navController.navigate(screen.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
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

            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.IntType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getInt("projectId") ?: return@composable
                val vm = koinViewModel<com.example.iobuild_kt.projects.presentation.project_detail.ProjectDetailViewModel>()
                IoScaffold(
                    currentRoute = Screen.ProjectList.route,
                    currentLang = currentLang,
                    onNavigate = { screen -> navController.navigate(screen.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
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
                IoScaffold(
                    currentRoute = Screen.ProjectList.route,
                    currentLang = currentLang,
                    onNavigate = { screen -> navController.navigate(screen.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onLanguageChange = onLanguageChange
                ) {
                    ProjectFormScreen(
                        projectId = actualId,
                        onBack = { navController.popBackStack() },
                        viewModel = vm
                    )
                }
            }

            // -- PLACEHOLDERS (also wrapped with IoScaffold) --
            composable(Screen.ClientList.route) {
                IoScaffold(currentRoute = Screen.ClientList.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }, onLanguageChange = onLanguageChange) {
                    ClientListScreen()
                }
            }
            composable(Screen.DeviceList.route) {
                IoScaffold(currentRoute = Screen.DeviceList.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }, onLanguageChange = onLanguageChange) {
                    DeviceListScreen()
                }
            }
            composable(Screen.Subscription.route) {
                IoScaffold(currentRoute = Screen.Subscription.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }, onLanguageChange = onLanguageChange) {
                    SubscriptionScreen()
                }
            }
            composable(Screen.Profile.route) {
                IoScaffold(currentRoute = Screen.Profile.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }, onLanguageChange = onLanguageChange) {
                    ProfileScreen()
                }
            }
            composable(Screen.Settings.route) {
                IoScaffold(currentRoute = Screen.Settings.route, currentLang = currentLang, onNavigate = { screen -> navController.navigate(screen.route) }, onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }, onLanguageChange = onLanguageChange) {
                    SettingsScreen()
                }
            }
        }
    }
}
