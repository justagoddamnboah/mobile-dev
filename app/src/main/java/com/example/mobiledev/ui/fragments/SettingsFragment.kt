package com.example.mobiledev.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobiledev.R
import com.example.mobiledev.databinding.FragmentSettingsBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.models.repositories.ProfilePictureRepository
import com.example.mobiledev.models.repositories.PonyRepository
import com.example.mobiledev.appsettings.prefs.BackupManager
import com.example.mobiledev.appsettings.prefs.FontManager
import com.example.mobiledev.appsettings.datastore.PaletteManager
import com.example.mobiledev.appsettings.prefs.ProfilePictureManager
import com.example.mobiledev.appsettings.datastore.SettingsDSStore
import com.example.mobiledev.appsettings.datastore.ThemeManager
import com.example.mobiledev.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var settingsDSStore: SettingsDSStore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDSStore = SettingsDSStore(requireContext().applicationContext)
        binding.btnBackToContent.setOnClickListener {
            findNavController().popBackStack()
        }
        setupTheme()
        setupFontSize()
        setupPalette()
        setupBackup()
        setupProfilePicture()
        updateBackupUI()
    }


    private fun setupTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            val isDark = settingsDSStore.loadDarkTheme().first()
            binding.switchDarkTheme.setOnCheckedChangeListener(null)
            binding.switchDarkTheme.isChecked = isDark
            binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
                viewLifecycleOwner.lifecycleScope.launch {
                    settingsDSStore.saveDarkTheme(isChecked)
                    ThemeManager.applyTheme(isChecked)
                }
            }
        }
    }


    private fun setupFontSize() {
        val currentScale = FontManager.getFontScale(requireContext())
        binding.seekFontSize.progress = FontManager.getProgressFromScale(currentScale)
        binding.seekFontSize.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        val newScale = FontManager.getScaleFromProgress(progress)

                        FontManager.setFontScale(requireContext(), newScale)
                        requireActivity().recreate()
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )
    }


    private fun setupPalette() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            PaletteManager.getPaletteNames()
        )
        binding.spinnerPalette.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            var currentPalette = settingsDSStore.loadPalette().first()
            val currentPosition = PaletteManager.getPositionByPaletteKey(currentPalette)
            binding.spinnerPalette.setSelection(currentPosition)
            binding.spinnerPalette.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val newPalette = PaletteManager.getPaletteKeyByPosition(position)
                        if (newPalette != currentPalette) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                settingsDSStore.savePalette(newPalette)
                                currentPalette = newPalette
                                requireActivity().recreate()
                            }
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }


    private fun setupBackup() {
        binding.btnCreateBackup.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val success = BackupManager.createBackup(
                    requireContext(),
                    PonyRepository.getBackupPonies()
                )
                BackupManager.setUseBackup(requireContext(), false)
                showBackupSnackbar(
                    success,
                    "Резервная копия создана",
                    "Ошибка создания"
                )
                if (success) {
                    updateBackupUI()
                }
            }
        }
        binding.btnRestoreBackup.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val restoredList = BackupManager.restoreBackup(requireContext())
                val success = restoredList != null
                if (success) {
                    BackupManager.setUseBackup(requireContext(), true)
                }
                showBackupSnackbar(
                    success,
                    "Данные успешно восстановлены",
                    "Ошибка восстановления"
                )
            }
        }
        binding.btnDeleteBackup.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val success = BackupManager.deleteBackup(requireContext())
                showBackupSnackbar(
                    success,
                    "Резервная копия удалена",
                    "Ошибка удаления"
                )
                if (success) {
                    updateBackupUI()
                }
            }
        }
    }


    private fun showBackupSnackbar(
        success: Boolean,
        successMsg: String,
        errorMsg: String
    ) {
        val message = if (success) successMsg else errorMsg
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }


    private fun updateBackupUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            val hasBackup = BackupManager.hasBackup(requireContext())
            binding.btnCreateBackup.visibility =
                if (hasBackup) View.GONE else View.VISIBLE
            binding.btnRestoreBackup.apply {
                visibility = if (hasBackup) View.VISIBLE else View.GONE
                isEnabled = hasBackup
            }
            binding.btnDeleteBackup.apply {
                visibility = if (hasBackup) View.VISIBLE else View.GONE
                isEnabled = hasBackup
            }
            binding.tvBackupStatus.apply {
                text = if (hasBackup) {
                    "Резервная копия найдена"
                } else {
                    "Резервная копия не найдена"
                }
                setTextColor(
                    requireContext().getColor(R.color.text_secondary)
                )
            }
            binding.tvBackupHint.text = if (hasBackup) {
                "Вы можете восстановить данные или удалить старую копию."
            } else {
                "Создайте копию, чтобы сохранить текущий список жителей Эквестрии."
            }
        }
    }


    private fun setupProfilePicture() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            ProfilePictureRepository.getProfilePictureNames()
        )
        binding.spinnerProfilePicture.adapter = adapter
        val currentProfilePicture =
            ProfilePictureManager.getProfilePicture(requireContext())
        binding.spinnerProfilePicture.setSelection(
            ProfilePictureRepository.getPositionByProfilePicture(
                currentProfilePicture
            )
        )
        binding.spinnerProfilePicture.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val selectedProfilePicture =
                        ProfilePictureRepository.getProfilePictureByPosition(pos)
                    if (selectedProfilePicture !=
                        ProfilePictureManager.getProfilePicture(requireContext())
                    ) {
                        ProfilePictureManager.saveProfilePicture(
                            requireContext(),
                            selectedProfilePicture
                        )
                        userViewModel.currentProfilePictureResId =
                            selectedProfilePicture
                        Snackbar.make(
                            binding.root,
                            "Аватарка обновлена!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}