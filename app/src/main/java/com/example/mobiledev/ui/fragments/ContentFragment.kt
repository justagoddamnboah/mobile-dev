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
import com.example.mobiledev.database.PonyDatabaseRepository
import com.example.mobiledev.databinding.FragmentContentBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.models.repositories.PonyRepository
import com.example.mobiledev.network.Pony
import com.example.mobiledev.network.PonyNetworkRepository
import com.example.mobiledev.ui.adapter.PonyAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentFragment : BaseFragment(R.layout.fragment_content) {

    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private val args: ContentFragmentArgs by navArgs()
    private val ponyAdapter = PonyAdapter()
    private lateinit var databaseRepository: PonyDatabaseRepository

    private val pageSize = 25
    private var nextOffset = 0
    private var isLoading = false
    private var isLoadingMore = false
    private var hasMoreData = true
    private var isInitialized = false
    private var isAuthorized = false
    private var errorText: String? = null
    private var cachedPonies: List<Pony> = emptyList()
    private var recyclerViewState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseRepository = PonyDatabaseRepository(requireContext().applicationContext)
        isAuthorized = args.username.isNotBlank() && args.username != "Незнакомец"
        isInitialized = false
        isLoading = false
        isLoadingMore = false
        cachedPonies = emptyList()
        errorText = null

        binding.recyclerPonies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPonies.adapter = ponyAdapter

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_content_to_settings)
        }
        binding.btnRefresh.setOnClickListener { loadPage(refresh = true) }
        binding.btnLoadMore.setOnClickListener { loadPage(refresh = false) }

        binding.btnSettings.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        binding.btnRefresh.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        renderContent()

        if (!isAuthorized) return

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                databaseRepository.poniesFlow.collect { ponies ->
                    cachedPonies = ponies
                    ponyAdapter.submitList(ponies)

                    PonyRepository.setPonies(ponies)
                    renderContent()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val savedPonies = databaseRepository.getCachedPonies()
            cachedPonies = savedPonies
            ponyAdapter.submitList(savedPonies)
            nextOffset = savedPonies.size
            hasMoreData = true
            isInitialized = true
            restoreScrollPosition()
            renderContent()

            if (savedPonies.isEmpty()) {
                loadPage(refresh = true)
            }
        }
    }

    private fun loadPage(refresh: Boolean) {
        if (!isAuthorized || !isInitialized || isLoading) return
        if (!refresh && !hasMoreData) return

        val requestedOffset = if (refresh) 0 else nextOffset
        isLoading = true
        isLoadingMore = !refresh
        errorText = null
        renderContent()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    PonyNetworkRepository.getPonies(
                        limit = pageSize,
                        offset = requestedOffset
                    )
                }

                result.fold(
                    onSuccess = { ponies ->
                        if (refresh) {
                            databaseRepository.replacePonies(ponies)
                            nextOffset = ponies.size
                        } else {
                            databaseRepository.savePonies(ponies)
                            nextOffset = requestedOffset + ponies.size
                        }
                        hasMoreData = ponies.size == pageSize
                        errorText = null

                        if (ponies.isEmpty() && !refresh) {
                            showMessage("Больше персонажей нет")
                        }
                    },
                    onFailure = { exception ->
                        errorText = "Ошибка загрузки: ${exception.message ?: "нет подключения"}"
                        showMessage("Не удалось загрузить данные. Проверьте интернет")
                    }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorText = "Ошибка сохранения: ${e.message ?: "неизвестная ошибка"}"
                showMessage("Не удалось сохранить данные")
            } finally {
                isLoading = false
                isLoadingMore = false
                if (_binding != null) renderContent()
            }
        }
    }

    private fun renderContent() {
        val ui = _binding ?: return

        if (!isAuthorized) {
            ui.attention.text =
                "Пони не готовы с вами знакомиться, так как они не знают вашего имени. Вы что, оборотень?"
            ui.attention.visibility = View.VISIBLE
            ui.recyclerPonies.visibility = View.GONE
            ui.progressBar.visibility = View.GONE
            ui.btnLoadMore.visibility = View.GONE
            ui.btnRefresh.visibility = View.GONE
            return
        }

        ui.attention.text = when {
            cachedPonies.isNotEmpty() ->
                "Привет, ${args.username}! Знакомься с жителями Эквестрии."
            !isInitialized || isLoading -> "Загружаем жителей Эквестрии..."
            errorText != null -> "$errorText\nПроверьте подключение к интернету."
            else -> "Данные отсутствуют. Нажмите «Обновить»."
        }
        ui.attention.visibility = View.VISIBLE
        ui.recyclerPonies.visibility =
            if (cachedPonies.isNotEmpty()) View.VISIBLE else View.GONE
        ui.progressBar.visibility =
            if (isLoading && cachedPonies.isEmpty()) View.VISIBLE else View.GONE

        ui.btnRefresh.isEnabled = isInitialized && !isLoading
        ui.btnLoadMore.visibility =
            if (isInitialized && cachedPonies.isNotEmpty() && hasMoreData) {
                View.VISIBLE
            } else {
                View.GONE
            }
        ui.btnLoadMore.isEnabled = !isLoading
        ui.btnLoadMore.text = if (isLoadingMore) "Загрузка..." else "Загрузить ещё"
    }

    private fun restoreScrollPosition() {
        recyclerViewState?.let { savedState ->
            binding.recyclerPonies.layoutManager?.onRestoreInstanceState(savedState)
            recyclerViewState = null
        }
    }

    private fun showMessage(text: String) {
        _binding?.let { Snackbar.make(it.root, text, Snackbar.LENGTH_LONG).show() }
    }

    override fun onPause() {
        if (_binding != null) {
            recyclerViewState =
                binding.recyclerPonies.layoutManager?.onSaveInstanceState()
        }
        super.onPause()
    }

    override fun onDestroyView() {
        binding.recyclerPonies.adapter = null
        super.onDestroyView()
        _binding = null
    }
}