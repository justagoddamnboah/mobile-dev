package com.example.mobiledev.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledev.R
import com.example.mobiledev.adapters.PonyAdapter
import com.example.mobiledev.databinding.FragmentContentBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.network.Pony
import com.example.mobiledev.network.PonyNetworkRepository
import com.example.mobiledev.prefs.BackupManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentFragment : BaseFragment(R.layout.fragment_content) {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private val args: ContentFragmentArgs by navArgs()
    private val ponyAdapter = PonyAdapter()

    private var currentPage = 0
    private var isLoading = false
    private var hasMoreData = true
    private var hasLoadedData = false
    private val pageSize = 25

    private var recyclerViewState: Parcelable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerPonies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPonies.adapter = ponyAdapter

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        val isAuthorized = args.username != "Незнакомец" && args.username.isNotEmpty()
        binding.btnSettings.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_content_to_settings)
        }

        binding.btnLoadMore.setOnClickListener {
            if (!isLoading && hasMoreData) {
                currentPage++
                loadPonies(isAppend = true)
            }
        }
        showAttention()

        if (isAuthorized && !hasLoadedData) {
            val backupPonies = getCurrentPonies()
            if (backupPonies.isNotEmpty()) {
                ponyAdapter.submitList(backupPonies, isAppend = false)
                binding.recyclerPonies.visibility = View.VISIBLE
                binding.btnLoadMore.visibility = View.GONE
                hasLoadedData = true
            } else {
                resetAndLoad()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isAuthorized = args.username != "Незнакомец" && args.username.isNotEmpty()
        binding.btnSettings.visibility = if (isAuthorized) View.VISIBLE else View.GONE

        if (hasLoadedData) {
            showAttention()
            if (ponyAdapter.itemCount > 0) {
                binding.recyclerPonies.visibility = View.VISIBLE
                binding.attention.visibility = View.VISIBLE
                binding.btnLoadMore.visibility = if (hasMoreData) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showAttention() {
        val name = args.username

        if (name == "Незнакомец" || name.isNullOrEmpty()) {
            binding.attention.text = "Пони не готовы с вами знакомиться, так как они не знают вашего имени. Вы что, оборотень?"
            binding.attention.visibility = View.VISIBLE
            binding.recyclerPonies.visibility = View.GONE
            binding.btnLoadMore.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.attention.text = "Привет, $name! Знакомься с жителями Эквестрии."
            binding.attention.visibility = View.VISIBLE
        }
    }

    private fun getCurrentPonies(): List<Pony> {
        return if (BackupManager.shouldUseBackup(requireContext())) {
            BackupManager.restoreBackup(requireContext()) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun resetAndLoad() {
        currentPage = 0
        hasMoreData = true
        isLoading = false
        hasLoadedData = false
        ponyAdapter.submitList(emptyList(), isAppend = false)
        loadPonies(isAppend = false)
    }

    private fun loadPonies(isAppend: Boolean) {
        if (isLoading) return
        isLoading = true

        if (!isAppend) {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerPonies.visibility = View.GONE
            binding.btnLoadMore.visibility = View.GONE
        } else {
            binding.btnLoadMore.isEnabled = false
            binding.btnLoadMore.text = "Загрузка..."
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val result = PonyNetworkRepository.getPonies(limit = pageSize, offset = currentPage * pageSize)

            withContext(Dispatchers.Main) {
                isLoading = false
                binding.progressBar.visibility = View.GONE
                binding.btnLoadMore.isEnabled = true
                binding.btnLoadMore.text = "Загрузить ещё"

                result.onSuccess { ponies ->
                    if (ponies.isEmpty()) {
                        hasMoreData = false
                        if (!isAppend) {
                            binding.attention.text = "Данные отсутствуют."
                            binding.attention.visibility = View.VISIBLE
                            binding.recyclerPonies.visibility = View.GONE
                        }
                        binding.btnLoadMore.visibility = View.GONE
                    } else {
                        if (!isAppend) {
                            binding.attention.text = "Привет, ${args.username}! Знакомься с жителями Эквестрии."
                        }

                        binding.attention.visibility = View.VISIBLE
                        binding.recyclerPonies.visibility = View.VISIBLE

                        ponyAdapter.submitList(ponies, isAppend = isAppend)
                        hasLoadedData = true

                        if (recyclerViewState != null && !isAppend) {
                            binding.recyclerPonies.layoutManager?.onRestoreInstanceState(recyclerViewState)
                            recyclerViewState = null
                        }

                        if (ponies.size < pageSize) {
                            hasMoreData = false
                            binding.btnLoadMore.visibility = View.GONE
                        } else {
                            hasMoreData = true
                            binding.btnLoadMore.visibility = View.VISIBLE
                        }
                    }
                }.onFailure { exception ->
                    hasMoreData = false
                    binding.attention.text = "Ошибка загрузки данных:\n${exception.message}\nПроверьте подключение к интернету."
                    binding.attention.visibility = View.VISIBLE
                    binding.recyclerPonies.visibility = View.GONE
                    binding.btnLoadMore.visibility = View.GONE

                    Snackbar.make(binding.root, "Не удалось загрузить данные", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val layoutManager = binding.recyclerPonies.layoutManager as? LinearLayoutManager
        recyclerViewState = layoutManager?.onSaveInstanceState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}