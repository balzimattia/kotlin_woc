package com.example.progettowoc.users.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class SettingPreferencesDataStore(private val context: Context) {
    // per il tema chiaro scuro
    private val Context.dataStore by preferencesDataStore(name = "setting_preferences")
    val DARK_THEME = booleanPreferencesKey("dark_theme")

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[DARK_THEME] = enabled }
    }

    fun darkThemeFlow(): Flow<Boolean> {
        return context.dataStore.data.map { it[DARK_THEME] ?: false }
    }
}