package com.example.mobiledev.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.databinding.ActivityBinding
import com.example.mobiledev.appsettings.prefs.FontManager
import com.example.mobiledev.appsettings.datastore.PaletteManager
import com.example.mobiledev.appsettings.datastore.SettingsDSStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SingleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBinding

    override fun attachBaseContext(newBase: Context) {
        val scale = FontManager.getFontScale(newBase)
        val config = newBase.resources.configuration
        config.fontScale = scale
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val settingsDSStore = SettingsDSStore(applicationContext)
        val isDark = runBlocking {
            settingsDSStore.loadDarkTheme().first()
        }
        val palette = runBlocking {
            settingsDSStore.loadPalette().first()
        }
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        super.onCreate(savedInstanceState)
        setTheme(PaletteManager.getThemeResByPalette(palette))

        binding = ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("SingleActivity", "onCreate()")
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragment) { v, insets ->
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