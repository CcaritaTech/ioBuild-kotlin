package com.example.iobuild_kt

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.iobuild_kt.core.data.TokenManager
import com.example.iobuild_kt.core.i18n.LanguageManager
import com.example.iobuild_kt.core.ui.navigation.NavGraph
import com.example.iobuild_kt.ui.theme.IoBuildktTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val languageManager: LanguageManager = koinInject()
            val currentLang by languageManager.currentLanguage.collectAsState(initial = "es")
            val scope = rememberCoroutineScope()
            val tokenManager: TokenManager = koinInject()
            val darkMode by tokenManager.darkModeEnabled.collectAsState(initial = false)
            var autoAuth by remember { mutableStateOf<Boolean?>(null) }

            // Biometric auto-auth
            LaunchedEffect(Unit) {
                val enabled = tokenManager.biometricEnabled.first()
                if (enabled && BiometricManager.from(this@MainActivity)
                        .canAuthenticate()
                    == BiometricManager.BIOMETRIC_SUCCESS) {
                    val executor = ContextCompat.getMainExecutor(this@MainActivity)
                    val callback = object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            autoAuth = true
                        }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            autoAuth = false
                        }
                        override fun onAuthenticationFailed() { }
                    }
                    val prompt = BiometricPrompt(this@MainActivity, executor, callback)
                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("IoBuild")
                        .setSubtitle("Verifica tu identidad")
                        .setNegativeButtonText("Usar contraseña")
                        .build()
                    prompt.authenticate(promptInfo)
                } else {
                    autoAuth = false
                }
            }

            if (autoAuth != null) {
                IoBuildktTheme(darkTheme = darkMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)
                    ) {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            currentLang = currentLang,
                            onLanguageChange = { scope.launch { languageManager.setLanguage(it) } },
                            startDestination = if (autoAuth == true) "dashboard" else "login"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    IoBuildktTheme {
        Surface(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
            val navController = rememberNavController()
            NavGraph(navController = navController, currentLang = "es", onLanguageChange = {})
        }
    }
}
