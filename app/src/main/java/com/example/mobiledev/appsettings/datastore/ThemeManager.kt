package com.example.mobiledev.appsettings.datastore

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}