package com.example.mobiledev.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mobiledev.R
import com.example.mobiledev.databinding.FragmentRegistrationBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class RegistrationFragment : BaseFragment(R.layout.fragment_registration) {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnToStart.setOnClickListener {
            registerProcess()
        }
    }

    private fun registerProcess() {
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if (username.isBlank()) {
            Snackbar.make(binding.root, "Введи имя", Snackbar.LENGTH_LONG).show()
            return
        }
        if (password.length < 4) {
            Snackbar.make(binding.root, "Пароль не должен быть меньше 4 символов", Snackbar.LENGTH_LONG).show()
            return
        }
        userViewModel.currentUsername = username
        userViewModel.currentPassword = password

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}