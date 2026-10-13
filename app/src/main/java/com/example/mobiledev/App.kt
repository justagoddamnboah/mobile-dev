package com.example.mobiledev

import android.app.Application
import com.example.mobiledev.prefs.ThemeManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.applyTheme(this)
    }
}