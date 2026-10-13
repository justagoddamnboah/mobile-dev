package com.example.mobiledev.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.R
import com.example.mobiledev.prefs.BackupManager
import com.example.mobiledev.prefs.ThemeManager
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : BaseActivity() {
    private lateinit var spinnerPalette: Spinner
    private lateinit var switchTheme: Switch
    private lateinit var tvBackupStatus: TextView
    private lateinit var tvBackupHint: TextView
    private lateinit var btnCreateBackup: Button
    private lateinit var btnRestoreBackup: Button
    private lateinit var btnDeleteBackup: Button

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
        switchTheme.isChecked = ThemeManager.getThemeState(this)
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setThemeState(this, isChecked)
        }

        tvBackupStatus = findViewById(R.id.tv_backup_status)
        tvBackupHint = findViewById(R.id.tv_backup_hint)
        btnCreateBackup = findViewById(R.id.btn_create_backup)
        btnRestoreBackup = findViewById(R.id.btn_restore_backup)
        btnDeleteBackup = findViewById(R.id.btn_delete_backup)

        updateBackupUI()

        btnCreateBackup.setOnClickListener {
            val success = BackupManager.createBackup(this, getDefaultPonies())
            if (success) {
                Snackbar.make(findViewById(R.id.settings), "Резервная копия успешно создана", Snackbar.LENGTH_SHORT).show()
                updateBackupUI()
            } else {
                Snackbar.make(findViewById(R.id.settings), "Ошибка при создании копии", Snackbar.LENGTH_SHORT).show()
            }
        }

        btnRestoreBackup.setOnClickListener {
            val restoredPonies = BackupManager.restoreBackup(this)
            if (restoredPonies != null) {
                Snackbar.make(findViewById(R.id.settings), "Данные успешно восстановлены!", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(findViewById(R.id.settings), "Ошибка при восстановлении данных", Snackbar.LENGTH_SHORT).show()
            }
        }

        btnDeleteBackup.setOnClickListener {
            val success = BackupManager.deleteBackup(this)
            if (success) {
                Snackbar.make(findViewById(R.id.settings), "Резервная копия удалена", Snackbar.LENGTH_SHORT).show()
                updateBackupUI()
            } else {
                Snackbar.make(findViewById(R.id.settings), "Ошибка при удалении копии", Snackbar.LENGTH_SHORT).show()
            }
        }

        spinnerPalette = findViewById(R.id.spinner_palette)
        setupPalette()
    }

    private fun setupPalette() {
        val palettes = arrayOf("Розовая", "Синяя", "Зеленая")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, palettes)
        spinnerPalette.adapter = adapter

        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val currentPalette = prefs.getString("palette", "pink") ?: "pink"

        val currentIndex = when (currentPalette) {
            "blue" -> 1
            "green" -> 2
            else -> 0
        }
        spinnerPalette.setSelection(currentIndex)

        spinnerPalette.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val paletteKey = when (pos) {
                    1 -> "blue"
                    2 -> "green"
                    else -> "pink"
                }

                if (paletteKey != currentPalette) {
                    prefs.edit().putString("palette", paletteKey).apply()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateBackupUI() {
        val hasBackup = BackupManager.hasBackup(this)

        btnRestoreBackup.isEnabled = hasBackup
        btnDeleteBackup.isEnabled = hasBackup

        if (hasBackup) {
            tvBackupStatus.text = "Резервная копия найдена"
            tvBackupStatus.setTextColor(getColor(R.color.text_secondary))
            tvBackupHint.text = "Вы можете восстановить данные или удалить старую копию."
        } else {
            tvBackupStatus.text = "Резервная копия не найдена"
            tvBackupHint.text = "Создайте копию, чтобы сохранить текущий список жителей Эквестрии."
        }
    }

    private fun getDefaultPonies(): List<com.example.mobiledev.models.Pony> {
        return listOf(
            com.example.mobiledev.models.Pony("Сумеречная Искорка", "Аликорн", "Женский", R.drawable.twilight_sparkle, android.graphics.Color.rgb(230, 210, 255)),
            com.example.mobiledev.models.Pony("Спайк", "Дракон", "Мужской", R.drawable.spike, android.graphics.Color.rgb(190, 235, 200))
        )
    }
}