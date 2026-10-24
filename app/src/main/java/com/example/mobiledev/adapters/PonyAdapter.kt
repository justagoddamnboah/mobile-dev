package com.example.mobiledev.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledev.databinding.PonyCardBinding
import com.example.mobiledev.models.Pony

class PonyAdapter : RecyclerView.Adapter<PonyAdapter.PonyViewHolder>() {

    private var ponies: List<Pony> = emptyList()

    fun submitList(newPonies: List<Pony>) {
        ponies = newPonies
        notifyDataSetChanged()
    }

    class PonyViewHolder(private val binding: PonyCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pony: Pony) {
            binding.ponyPhoto.setImageResource(pony.photoSrc)
            binding.ponyPhoto.contentDescription = pony.name
            binding.ponyName.text = pony.name
            binding.ponyKind.text = "Вид: ${pony.kind}"
            binding.ponyGender.text = "Пол: ${pony.gender}"
            binding.ponyColorBar.setBackgroundColor(pony.color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PonyViewHolder {
        val binding = PonyCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PonyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PonyViewHolder, position: Int) {
        holder.bind(ponies[position])
    }

    override fun getItemCount(): Int = ponies.size
}