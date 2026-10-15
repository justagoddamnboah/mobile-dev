package com.example.mobiledev.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mobiledev.prefs.FontManager
import com.example.mobiledev.prefs.PaletteManager
import com.example.mobiledev.prefs.ThemeManager

abstract class BaseActivity : AppCompatActivity() {
    private var appliedPalette: String? = null
    private var appliedFontScale: Float = 1.0f

    private val logTag: String
        get() = "Lifecycle_${this::class.java.simpleName}"

    override fun attachBaseContext(newBase: Context) {
        Log.d(logTag, "attachBaseContext")
        val fontScale = FontManager.getFontScale(newBase)
        val config = Configuration(newBase.resources.configuration)
        config.fontScale = fontScale
        newBase.resources.updateConfiguration(config, newBase.resources.displayMetrics)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "onCreate, savedInstanceState=${savedInstanceState != null}")
        ThemeManager.applyTheme(this)
        val palette = PaletteManager.getPalette(this)
        appliedPalette = palette
        appliedFontScale = FontManager.getFontScale(this)
        val themeRes = PaletteManager.getThemeResByPalette(palette)
        setTheme(themeRes)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Log.d(logTag, "onStart")
    }

    override fun onResume() {
        Log.d(logTag, "onResume")
        super.onResume()
        val currentPalette = PaletteManager.getPalette(this)
        val currentFontScale = FontManager.getFontScale(this)
        if (appliedPalette != currentPalette || appliedFontScale != currentFontScale) {
            Log.d(logTag, "Обнаружены изменения настроек (тема/шрифт), вызываем recreate()")
            recreate()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(logTag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(logTag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(logTag, "onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(logTag, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(logTag, "onRestoreInstanceState")
    }
}