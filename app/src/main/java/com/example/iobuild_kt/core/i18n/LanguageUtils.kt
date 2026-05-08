package com.example.iobuild_kt.core.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.firstOrNull

val LocalLanguage = staticCompositionLocalOf { "es" }

@Composable
fun lang(key: String): String {
    val currentLang = LocalLanguage.current
    return Translations.get(key, currentLang)
}
