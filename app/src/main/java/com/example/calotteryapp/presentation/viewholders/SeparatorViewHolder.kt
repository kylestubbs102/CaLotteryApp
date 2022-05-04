package com.example.calotteryapp.presentation.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.example.calotteryapp.databinding.SeparatorItemBinding

class SeparatorViewHolder(
    private val binding: SeparatorItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(text: String) {
        binding.textviewSeparator.text = text
    }
}
