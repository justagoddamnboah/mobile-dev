package com.example.mobiledev.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.R
import com.example.mobiledev.models.Pony
import com.example.mobiledev.prefs.BackupManager
import com.example.mobiledev.prefs.FontManager
import com.example.mobiledev.prefs.PaletteManager
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

        setupBackup()

        spinnerPalette = findViewById(R.id.spinner_palette)
        setupPalette()
        setupFontSize()
    }

    private fun setupFontSize() {
        val seekFontSize = findViewById<SeekBar>(R.id.seek_font_size)
        val currentScale = FontManager.getFontScale(this)
        seekFontSize.progress = FontManager.getProgressFromScale(currentScale)

        seekFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newScale = FontManager.getScaleFromProgress(progress)
                    FontManager.setFontScale(this@SettingsActivity, newScale)
                    recreate()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupPalette() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, PaletteManager.getPaletteNames())
        spinnerPalette.adapter = adapter

        val currentPalette = PaletteManager.getPalette(this)
        spinnerPalette.setSelection(PaletteManager.getPositionByPaletteKey(currentPalette))

        spinnerPalette.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val newPaletteKey = PaletteManager.getPaletteKeyByPosition(pos)
                val actualCurrentPalette = PaletteManager.getPalette(this@SettingsActivity)

                if (newPaletteKey != actualCurrentPalette) {
                    PaletteManager.setPalette(this@SettingsActivity, newPaletteKey)
                    recreate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupBackup() {
        val rootView = findViewById<View>(R.id.settings)

        btnCreateBackup.setOnClickListener {
            val success = BackupManager.createBackup(this, getBackupPonies())

            BackupManager.setUseBackup(this, false)

            showBackupSnackbar(rootView, success, "Резервная копия создана", "Ошибка создания")
            if (success) updateBackupUI()
        }

        btnRestoreBackup.setOnClickListener {
            val restoredList = BackupManager.restoreBackup(this)
            val success = restoredList != null

            if (success) {
                BackupManager.setUseBackup(this, true)
            }

            showBackupSnackbar(rootView, success, "Данные успешно восстановлены", "Ошибка восстановления")
        }

        btnDeleteBackup.setOnClickListener {
            val success = BackupManager.deleteBackup(this)

            showBackupSnackbar(rootView, success, "Резервная копия удалена", "Ошибка удаления")
            if (success) updateBackupUI()
        }
    }

    private fun showBackupSnackbar(view: View, success: Boolean, successMsg: String, errorMsg: String) {
        val message = if (success) successMsg else errorMsg
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun updateBackupUI() {
        val hasBackup = BackupManager.hasBackup(this)
        btnRestoreBackup.apply {
            visibility = if (hasBackup) View.VISIBLE else View.GONE
            isEnabled = hasBackup
        }
        btnDeleteBackup.apply {
            visibility = if (hasBackup) View.VISIBLE else View.GONE
            isEnabled = hasBackup
        }
        tvBackupStatus.apply {
            text = if (hasBackup) "Резервная копия найдена" else "Резервная копия не найдена"
            setTextColor(getColor(R.color.text_secondary))
        }
        tvBackupHint.text = if (hasBackup) {
            "Вы можете восстановить данные или удалить старую копию."
        } else {
            "Создайте копию, чтобы сохранить текущий список жителей Эквестрии."
        }
    }

    private fun getBackupPonies(): List<Pony> {
        return listOf(
            Pony("Сумеречная Искорка", "Аликорн", "Женский", R.drawable.twilight_sparkle, android.graphics.Color.rgb(230, 210, 255)),
            Pony("Спайк", "Дракон", "Мужской", R.drawable.spike, android.graphics.Color.rgb(190, 235, 200))
        )
    }
}