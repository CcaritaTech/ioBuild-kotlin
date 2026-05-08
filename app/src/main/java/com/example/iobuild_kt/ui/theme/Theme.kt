package com.example.iobuild_kt.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Neutral100,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,

    secondary = Teal40,
    onSecondary = Neutral100,
    secondaryContainer = Teal90,
    onSecondaryContainer = Color(0xFF003333),

    tertiary = Amber40,
    onTertiary = Neutral100,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,

    background = GreenBg,
    onBackground = Neutral10,
    surface = Neutral100,
    onSurface = Neutral10,
    surfaceVariant = GreenSurfaceVariant,
    onSurfaceVariant = Color(0xFF4B5563),

    error = Error40,
    onError = Neutral100,
    errorContainer = Error90,
    onErrorContainer = Color(0xFF410002),
    outline = GreenOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = Green50,
    onPrimary = DarkGreen10,
    primaryContainer = DarkGreen30,
    onPrimaryContainer = Green80,

    secondary = Teal80,
    onSecondary = Color(0xFF003333),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Teal90,

    tertiary = Amber80,
    onTertiary = Amber10,
    tertiaryContainer = Color(0xFF4A2800),
    onTertiaryContainer = Amber90,

    background = Color(0xFF111827),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFFD1D5DB),

    error = Error80,
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Error80,
    outline = Color(0xFF6B7280)
)

@Composable
fun IoBuildktTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
