package com.example.mobiledev.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.R
import com.example.mobiledev.prefs.ThemeHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchTheme: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btn_back_to_content).setOnClickListener { finish() }

        switchTheme = findViewById(R.id.switch_dark_theme)

        switchTheme.isChecked = ThemeHelper.getThemeState(this)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemeHelper.setThemeState(this, isChecked)
        }
    }
}