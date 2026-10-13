package com.example.mobiledev.prefs

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object ThemeHelper {
    private const val PREFS_NAME = "ThemePrefs"
    private const val KEY_IS_DARK = "isDarkTheme"

    fun applyTheme(context: Context) {
        val isDark = getThemeState(context)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun getThemeState(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_DARK, false)
    }

    fun setThemeState(context: Context, isDark: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_IS_DARK, isDark)
        }
        applyTheme(context)
    }
}