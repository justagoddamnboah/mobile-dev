package com.example.mobiledev

import android.app.Application
import com.example.mobiledev.prefs.ThemeHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeHelper.applyTheme(this)
    }
}