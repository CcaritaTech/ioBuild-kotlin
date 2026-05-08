package com.example.iobuild_kt.core.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Login : Screen("login", "Iniciar Sesión")
    object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Dashboard)
    object ProjectList : Screen("projects", "Proyectos", Icons.Default.Quiz)
    object ProjectDetail : Screen("projects/{projectId}", "Detalle del Proyecto") {
        fun createRoute(projectId: Int) = "projects/$projectId"
    }
    object ProjectForm : Screen("projects/form?projectId={projectId}", "Formulario Proyecto") {
        fun createRoute(projectId: Int? = null) =
            if (projectId != null) "projects/form?projectId=$projectId"
            else "projects/form"
    }
    object ClientList : Screen("clients", "Clientes", Icons.Default.Group)
    object ClientDetail : Screen("clients/{clientId}", "Detalle del Cliente") {
        fun createRoute(clientId: Int) = "clients/$clientId"
    }
    object DeviceList : Screen("devices", "Dispositivos", Icons.Default.Quiz)
    object Subscription : Screen("subscription", "Suscripción", Icons.Default.CreditCard)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object Settings : Screen("settings", "Configuración", Icons.Default.Settings)
}
