package com.example.mobiledev.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobiledev.R
import com.example.mobiledev.databinding.FragmentContentBinding
import com.example.mobiledev.extensions.BaseFragment
import com.example.mobiledev.models.Pony
import com.example.mobiledev.prefs.BackupManager

class ContentFragment : BaseFragment(R.layout.fragment_content) {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!
    private val args: ContentFragmentArgs by navArgs()

    private fun getCurrentPonies(): List<Pony> {
        return if (BackupManager.shouldUseBackup(requireContext())) {
            BackupManager.restoreBackup(requireContext()) ?: getAllPonies()
        } else {
            getAllPonies()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_content_to_settings)
        }

        showAttention()
    }

    override fun onResume() {
        super.onResume()
        showAttention()
    }

    private fun showAttention() {
        val name = args.username
        if (name == "Незнакомец" || name.isEmpty()) {
            binding.attention.text = "Пони не готовы с вами знакомиться, так как они не знают вашего имени. Вы что, оборотень?"
            binding.poniesContainer.removeAllViews()
        } else {
            binding.attention.text = "Привет, $name! Знакомься с жителями Эквестрии."
            showPonies()
        }
    }

    private fun showPonies() {
        binding.poniesContainer.removeAllViews()
        val ponies = getCurrentPonies()
        if (ponies.isEmpty()) {
            if (BackupManager.hasBackup(requireContext())) {
                binding.attention.text = "Данные повреждены, но найдена резервная копия."
            } else {
                binding.attention.text = "Данные отсутствуют."
            }
            return
        }

        ponies.forEach { pony ->
            val card = layoutInflater.inflate(R.layout.pony_card, binding.poniesContainer, false)
            card.findViewById<ImageView>(R.id.pony_photo).apply {
                setImageResource(pony.photoSrc)
                contentDescription = pony.name
            }
            card.findViewById<TextView>(R.id.pony_name).text = pony.name
            card.findViewById<TextView>(R.id.pony_kind).text = "Вид: ${pony.kind}"
            card.findViewById<TextView>(R.id.pony_sex).text = "Пол: ${pony.sex}"
            card.findViewById<View>(R.id.pony_color_bar).setBackgroundColor(pony.color)
            binding.poniesContainer.addView(card)
        }
    }

    private fun getAllPonies(): List<Pony> {
        return listOf(
            Pony("Сумеречная Искорка", "Аликорн", "Женский", R.drawable.twilight_sparkle, Color.rgb(230, 210, 255)),
            Pony("Спайк", "Дракон", "Мужской", R.drawable.spike, Color.rgb(190, 235, 200)),
            Pony("Радуга Дэш", "Пегас", "Женский", R.drawable.rainbow_dash, Color.rgb(190, 225, 255)),
            Pony("Эпплджек", "Земная пони", "Женский", R.drawable.applejack, Color.rgb(255, 225, 170)),
            Pony("Рарити", "Единорог", "Женский", R.drawable.rarity, Color.rgb(230, 215, 255)),
            Pony("Флаттершай", "Пегас", "Женский", R.drawable.fluttershy, Color.rgb(255, 235, 170)),
            Pony("Пинки Пай", "Земная пони", "Женский", R.drawable.pinkie_pie, Color.rgb(255, 190, 220)),
            Pony("Эппл Блум", "Земная пони", "Женский", R.drawable.apple_bloom, Color.rgb(255, 205, 130)),
            Pony("Свити Белль", "Единорог", "Женский", R.drawable.sweetie_belle, Color.rgb(245, 225, 240)),
            Pony("Скуталу", "Пегас", "Женский", R.drawable.scootaloo, Color.rgb(255, 175, 125))
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}