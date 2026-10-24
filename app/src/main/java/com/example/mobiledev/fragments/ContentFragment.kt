package com.example.mobiledev.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledev.R
import com.example.mobiledev.adapters.PonyAdapter
import com.example.mobiledev.databinding.FragmentContentBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.models.Pony
import com.example.mobiledev.models.PonyRepository
import com.example.mobiledev.prefs.BackupManager

class ContentFragment : BaseFragment(R.layout.fragment_content) {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private val args: ContentFragmentArgs by navArgs()
    private val ponyAdapter = PonyAdapter()

    private fun getCurrentPonies(): List<Pony> {
        return if (BackupManager.shouldUseBackup(requireContext())) {
            BackupManager.restoreBackup(requireContext()) ?: PonyRepository.getAllPonies()
        } else {
            PonyRepository.getAllPonies()
        }
    }

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
        showAttention()
    }

    override fun onResume() {
        super.onResume()
        val isAuthorized = args.username != "Незнакомец" && args.username.isNotEmpty()
        binding.btnSettings.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        showAttention()
    }

    private fun showAttention() {
        val name = args.username
        if (name == "Незнакомец" || name.isEmpty()) {
            binding.attention.text = "Пони не готовы с вами знакомиться, так как они не знают вашего имени. Вы что, оборотень?"
            binding.recyclerPonies.visibility = View.GONE
        } else {
            binding.attention.text = "Привет, $name! Знакомься с жителями Эквестрии."
            binding.recyclerPonies.visibility = View.VISIBLE
            showPonies()
        }
    }

    private fun showPonies() {
        val currentPonies = getCurrentPonies()

        if (currentPonies.isEmpty()) {
            binding.attention.text = if (BackupManager.hasBackup(requireContext())) {
                "Найдена резервная копия."
            } else {
                "Данные отсутствуют."
            }
            binding.recyclerPonies.visibility = View.GONE
            return
        }

        ponyAdapter.submitList(currentPonies)
        binding.recyclerPonies.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}