package com.example.mobiledev.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mobiledev.R
import com.example.mobiledev.databinding.FragmentStartBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.viewmodel.UserViewModel

class StartFragment : BaseFragment(R.layout.fragment_start) {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateGreeting()

        binding.btnToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_start_to_registration)
        }

        binding.btnToContent.setOnClickListener {
            val name = userViewModel.currentUsername ?: "Незнакомец"

            val action = StartFragmentDirections.actionStartToContent(name)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        updateGreeting()
    }

    private fun updateGreeting() {
        val name = userViewModel.currentUsername
        if (name != null) {
            binding.ponyQuote.text = "Привет, $name!"
        } else {
            binding.ponyQuote.text = "Добро пожаловать в Эквестрию! Здесь ты можешь познакомиться со всеми жителями!"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}