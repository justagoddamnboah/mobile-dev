package com.example.mobiledev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.prefs.FontManager
import com.example.mobiledev.prefs.PaletteManager
import com.example.mobiledev.prefs.ThemeManager

class SingleActivity : AppCompatActivity(R.layout.activity) {

    override fun attachBaseContext(newBase: Context) {
        val scale = FontManager.getFontScale(newBase)
        val config = newBase.resources.configuration
        config.fontScale = scale
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        val palette = PaletteManager.getPalette(this)
        setTheme(PaletteManager.getThemeResByPalette(palette))

        super.onCreate(savedInstanceState)
        Log.d("SingleActivity", "onCreate()")
        enableEdgeToEdge()

        val navHostView = findViewById<View>(R.id.nav_host_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(navHostView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() { super.onStart(); Log.d("SingleActivity", "onStart()") }
    override fun onResume() { super.onResume(); Log.d("SingleActivity", "onResume()") }
    override fun onPause() { super.onPause(); Log.d("SingleActivity", "onPause()") }
    override fun onStop() { super.onStop(); Log.d("SingleActivity", "onStop()") }
    override fun onDestroy() { super.onDestroy(); Log.d("SingleActivity", "onDestroy()") }
}