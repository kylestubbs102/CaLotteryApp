package com.example.calotteryapp.presentation.lotterydraws.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calotteryapp.R

class SeparatorViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {
    private var separatorTextView: TextView = view.findViewById(R.id.separatorTextView)

    fun bind(text: String) {
        separatorTextView.text = text
    }

}
