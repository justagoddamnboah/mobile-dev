package com.example.mobiledev.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledev.R
import com.example.mobiledev.appsettings.prefs.BackupManager
import com.example.mobiledev.databinding.FragmentContentBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.network.Pony
import com.example.mobiledev.network.PonyNetworkRepository
import com.example.mobiledev.storage.sqlite.SQLiteManager
import com.example.mobiledev.ui.adapter.PonyAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentFragment : BaseFragment(R.layout.fragment_content) {

    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private val args: ContentFragmentArgs by navArgs()
    private val ponyAdapter = PonyAdapter()
    private val storage: SQLiteManager = SQLiteManager()

    private val pageSize = 25
    private var isAuthorized = false
    private var isInitialized = false
    private var isLoading = false
    private var hasMoreData = true
    private var hasBackup = false
    private var backupChecked = false
    private var cachedPonies: List<Pony> = emptyList()
    private var errorText: String? = null
    private var recyclerViewState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isAuthorized = args.username.isNotBlank() && args.username != "Незнакомец"
        isInitialized = false
        isLoading = false
        cachedPonies = emptyList()
        hasMoreData = true
        backupChecked = false
        errorText = null

        ponyAdapter.submitList(emptyList())
        binding.recyclerPonies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPonies.adapter = ponyAdapter

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_content_to_settings)
        }
        binding.btnRefresh.setOnClickListener { loadFromApi(refresh = true) }
        binding.btnLoadMore.setOnClickListener { loadFromApi(refresh = false) }

        binding.btnSettings.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        binding.btnRefresh.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        render()

        if (!isAuthorized) return

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                storage.load().collect { ponies ->
                    cachedPonies = ponies
                    ponyAdapter.submitList(ponies)
                    if (recyclerViewState != null && ponies.isNotEmpty()) {
                        binding.recyclerPonies.layoutManager
                            ?.onRestoreInstanceState(recyclerViewState)
                        recyclerViewState = null
                    }
                    render()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Холодный старт: сначала смотрим Room.
            val local = storage.load().first()
            isInitialized = true
            cachedPonies = local
            ponyAdapter.submitList(local)
            render()
            if (local.isEmpty()) loadFromApi(refresh = true)
        }
    }

    private fun loadFromApi(refresh: Boolean) {
        if (!isAuthorized || !isInitialized || isLoading) return
        if (!refresh && (!backupChecked || hasBackup || !hasMoreData)) return

        val offset = if (refresh) 0 else cachedPonies.size
        isLoading = true
        errorText = null
        render()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    PonyNetworkRepository.getPonies(limit = pageSize, offset = offset)
                }
                val page = response.getOrThrow()
                if (page.isNotEmpty()) {
                    val current = storage.load().first()
                    val merged = (current + page).associateBy { it.id }.values.toList()
                    storage.save(merged)
                }
                hasMoreData = page.size == pageSize
                if (page.isEmpty() && !refresh) showMessage("Больше персонажей нет")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorText = "Ошибка загрузки: ${e.message ?: "нет подключения"}"
                showMessage("Не удалось загрузить данные. Сохранённые данные не удалены")
            } finally {
                isLoading = false
                if (_binding != null) render()
            }
        }
    }

    private fun render() {
        val ui = _binding ?: return

        if (!isAuthorized) {
            ui.attention.text =
                "Пони не готовы с вами знакомиться, так как они не знают вашего имени. Вы что, оборотень?"
            ui.attention.visibility = View.VISIBLE
            ui.recyclerPonies.visibility = View.GONE
            ui.btnLoadMore.visibility = View.GONE
            ui.progressBar.visibility = View.GONE
            return
        }

        ui.attention.text = when {
            errorText != null && cachedPonies.isNotEmpty() ->
                "Привет, ${args.username}! Список сохранён. $errorText"
            cachedPonies.isNotEmpty() ->
                "Привет, ${args.username}! Знакомься с жителями Эквестрии."
            !isInitialized || isLoading -> "Загружаем жителей Эквестрии..."
            errorText != null -> errorText
            else -> "Данные отсутствуют. Нажмите «Обновить»."
        }
        ui.attention.visibility = View.VISIBLE
        ui.recyclerPonies.visibility =
            if (cachedPonies.isNotEmpty()) View.VISIBLE else View.GONE
        ui.progressBar.visibility =
            if (isLoading && cachedPonies.isEmpty()) View.VISIBLE else View.GONE
        ui.btnRefresh.isEnabled = isInitialized && !isLoading
        ui.btnLoadMore.visibility =
            if (isInitialized && backupChecked && !hasBackup &&
                hasMoreData && cachedPonies.isNotEmpty()) View.VISIBLE else View.GONE
        ui.btnLoadMore.isEnabled = !isLoading
    }

    override fun onResume() {
        super.onResume()
        if (!isAuthorized || _binding == null) return
        backupChecked = false
        render()
        viewLifecycleOwner.lifecycleScope.launch {
            val exists = BackupManager.hasBackup(requireContext().applicationContext)
            if (_binding != null) {
                hasBackup = exists
                backupChecked = true
                render()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (_binding != null) {
            recyclerViewState = (binding.recyclerPonies.layoutManager
                as? LinearLayoutManager)?.onSaveInstanceState()
        }
    }

    private fun showMessage(message: String) {
        _binding?.let { Snackbar.make(it.root, message, Snackbar.LENGTH_LONG).show() }
    }

    override fun onDestroyView() {
        binding.recyclerPonies.adapter = null
        super.onDestroyView()
        _binding = null
    }
}