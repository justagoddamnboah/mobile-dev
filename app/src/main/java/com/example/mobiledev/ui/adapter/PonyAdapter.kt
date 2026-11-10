package com.example.mobiledev.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mobiledev.databinding.PonyCardBinding
import com.example.mobiledev.network.Pony

class PonyAdapter : RecyclerView.Adapter<PonyAdapter.PonyViewHolder>() {

    private val ponies = mutableListOf<Pony>()

    fun submitList(newPonies: List<Pony>, isAppend: Boolean = false) {
        if (isAppend) {
            val startPosition = ponies.size
            ponies.addAll(newPonies)
            notifyItemRangeInserted(startPosition, newPonies.size)
        } else {
            ponies.clear()
            ponies.addAll(newPonies)
            notifyDataSetChanged()
        }
    }

    class PonyViewHolder(
        private val binding: PonyCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pony: Pony) {
            binding.ponyName.text = pony.name
            binding.ponyKind.text =
                "Вид: ${pony.kind.joinToString(", ").ifBlank { "Неизвестно" }}"
            val sexRu = when (pony.sex?.lowercase()) {
                "male" -> "Мужской"
                "female" -> "Женский"
                else -> "Неизвестно"
            }
            binding.ponySex.text = "Пол: $sexRu"
            binding.ponyResidence.text =
                "Место проживания: ${pony.residence ?: "Неизвестно"}"
            if (pony.imageUrl.isNotEmpty()) {
                binding.ponyPhoto.load(pony.imageUrl) {
                    crossfade(true)
                }
            } else {
                binding.ponyPhoto.setImageDrawable(null)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PonyViewHolder {
        val binding = PonyCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PonyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PonyViewHolder,
        position: Int
    ) {
        holder.bind(ponies[position])
    }

    override fun getItemCount(): Int = ponies.size
}