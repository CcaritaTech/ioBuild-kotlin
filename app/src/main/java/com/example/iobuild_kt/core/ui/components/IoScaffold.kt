package com.example.iobuild_kt.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.iobuild_kt.core.data.TokenManager
import com.example.iobuild_kt.core.i18n.Translations
import com.example.iobuild_kt.core.i18n.lang
import com.example.iobuild_kt.core.ui.navigation.Screen
import com.example.iobuild_kt.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class NavItem(val screen: Screen, val icon: ImageVector, val labelKey: String)

private val builderNavItems = listOf(
    NavItem(Screen.Dashboard, Icons.Default.Dashboard, "nav.home"),
    NavItem(Screen.Profile, Icons.Default.Person, "nav.profile"),
    NavItem(Screen.ProjectList, Icons.Default.Quiz, "nav.projects"),
    NavItem(Screen.ClientList, Icons.Default.Group, "nav.clients"),
    NavItem(Screen.DeviceList, Icons.Default.Build, "nav.devices"),
    NavItem(Screen.Subscription, Icons.Default.CreditCard, "nav.subscription"),
    NavItem(Screen.Settings, Icons.Default.Settings, "nav.configuration"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoScaffold(
    currentRoute: String?,
    currentLang: String,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit,
    onLanguageChange: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Load real profile data
    val tokenManager: TokenManager = koinInject()
    val profileRepository: ProfileRepository = koinInject()
    var userName by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val userId = tokenManager.userId.firstOrNull()
        if (userId != null) {
            profileRepository.getProfile(userId).onSuccess { profile ->
                userName = profile.name
                userPhotoUrl = profile.photoUrl.ifEmpty { null }
            }
        }
        if (userName.isBlank()) userName = "Constructor"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(userName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            Text(lang("role.builder"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    builderNavItems.forEach { item ->
                        val isSelected = currentRoute == item.screen.route
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(lang(item.labelKey)) },
                            selected = isSelected,
                            onClick = { onNavigate(item.screen); scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    Spacer(Modifier.weight(1f))
                    Text(lang("nav.language"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                    Translations.getAvailableLanguages().forEach { (code, label) ->
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Language, contentDescription = null) },
                            label = { Text(label) },
                            selected = currentLang == code,
                            onClick = { onLanguageChange(code); scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }

                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        label = { Text(lang("nav.logout")) },
                        selected = false,
                        onClick = onLogout,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("IoBuild", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, contentDescription = null) } },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) { content() }
        }
    }
}
