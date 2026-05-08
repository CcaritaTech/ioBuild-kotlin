package com.example.iobuild_kt.core.i18n

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.langStore by preferencesDataStore(name = "iobuild_lang")

class LanguageManager(private val context: Context) {
    companion object {
        private val KEY_LANG = stringPreferencesKey("language")
    }

    val currentLanguage: Flow<String> = context.langStore.data.map { prefs ->
        prefs[KEY_LANG] ?: "en"
    }

    suspend fun setLanguage(lang: String) {
        context.langStore.edit { prefs ->
            prefs[KEY_LANG] = lang
        }
    }
}
