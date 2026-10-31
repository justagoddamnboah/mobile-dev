package com.example.mobiledev.appsettings.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mobiledev.MobileApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DS")

class SettingsDSStore(applicationContext: Context) {
    suspend fun saveDarkTheme(isDark: Boolean) {
        MobileApplication.mobileApplicationContext().dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = isDark
        }
    }

    fun loadDarkTheme(): Flow<Boolean> =
        MobileApplication.mobileApplicationContext().dataStore.data.map { preferences ->
            preferences[IS_DARK_THEME] ?: false
        }

    suspend fun savePalette(palette: String) {
        MobileApplication.mobileApplicationContext().dataStore.edit { preferences ->
            preferences[PALETTE] = palette
        }
    }

    fun loadPalette(): Flow<String> =
        MobileApplication.mobileApplicationContext().dataStore.data.map { preferences ->
            preferences[PALETTE] ?: "Розовая"
        }

    companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val PALETTE = stringPreferencesKey("palette")
    }
}