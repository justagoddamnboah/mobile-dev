package com.example.mobiledev.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.example.mobiledev.R
import com.example.mobiledev.databinding.FragmentSettingsBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.models.Pony
import com.example.mobiledev.models.PonyRepository
import com.example.mobiledev.prefs.BackupManager
import com.example.mobiledev.prefs.FontManager
import com.example.mobiledev.prefs.PaletteManager
import com.example.mobiledev.prefs.ThemeManager
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToContent.setOnClickListener { findNavController().popBackStack() }

        binding.switchDarkTheme.isChecked = ThemeManager.getThemeState(requireContext())
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setThemeState(requireContext(), isChecked)
        }
        setupFontSize()
        setupPalette()
        setupBackup()
        updateBackupUI()
    }

    private fun setupFontSize() {
        val currentScale = FontManager.getFontScale(requireContext())
        binding.seekFontSize.progress = FontManager.getProgressFromScale(currentScale)
        binding.seekFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newScale = FontManager.getScaleFromProgress(progress)
                    FontManager.setFontScale(requireContext(), newScale)
                    requireActivity().recreate() // Пересоздаем Activity для применения
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupPalette() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, PaletteManager.getPaletteNames())
        binding.spinnerPalette.adapter = adapter

        val currentPalette = PaletteManager.getPalette(requireContext())
        binding.spinnerPalette.setSelection(PaletteManager.getPositionByPaletteKey(currentPalette))

        binding.spinnerPalette.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val newPaletteKey = PaletteManager.getPaletteKeyByPosition(pos)
                val actualCurrentPalette = PaletteManager.getPalette(requireContext())
                if (newPaletteKey != actualCurrentPalette) {
                    PaletteManager.setPalette(requireContext(), newPaletteKey)
                    requireActivity().recreate() // Пересоздаем Activity для применения темы
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupBackup() {
        binding.btnCreateBackup.setOnClickListener {
            val success = BackupManager.createBackup(requireContext(), PonyRepository.getBackupPonies())
            BackupManager.setUseBackup(requireContext(), false)
            showBackupSnackbar(success, "Резервная копия создана", "Ошибка создания")
            if (success) updateBackupUI()
        }
        binding.btnRestoreBackup.setOnClickListener {
            val restoredList = BackupManager.restoreBackup(requireContext())
            val success = restoredList != null
            if (success) BackupManager.setUseBackup(requireContext(), true)
            showBackupSnackbar(success, "Данные успешно восстановлены", "Ошибка восстановления")
        }
        binding.btnDeleteBackup.setOnClickListener {
            val success = BackupManager.deleteBackup(requireContext())
            showBackupSnackbar(success, "Резервная копия удалена", "Ошибка удаления")
            if (success) updateBackupUI()
        }
    }

    private fun showBackupSnackbar(success: Boolean, successMsg: String, errorMsg: String) {
        val message = if (success) successMsg else errorMsg
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun updateBackupUI() {
        val hasBackup = BackupManager.hasBackup(requireContext())
        binding.btnRestoreBackup.apply {
            visibility = if (hasBackup) View.VISIBLE else View.GONE
            isEnabled = hasBackup
        }
        binding.btnDeleteBackup.apply {
            visibility = if (hasBackup) View.VISIBLE else View.GONE
            isEnabled = hasBackup
        }
        binding.tvBackupStatus.apply {
            text = if (hasBackup) "Резервная копия найдена" else "Резервная копия не найдена"
            setTextColor(requireContext().getColor(R.color.text_secondary))
        }
        binding.tvBackupHint.text = if (hasBackup) {
            "Вы можете восстановить данные или удалить старую копию."
        } else {
            "Создайте копию, чтобы сохранить текущий список жителей Эквестрии."
        }
    }

    private fun getBackupPonies(): List<Pony> {
        return listOf(
            Pony("Сумеречная Искорка", "Аликорн", "Женский", R.drawable.twilight_sparkle, Color.rgb(230, 210, 255)),
            Pony("Спайк", "Дракон", "Мужской", R.drawable.spike, Color.rgb(190, 235, 200))
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}