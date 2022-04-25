package com.example.calotteryapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calotteryapp.R
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.presentation.viewholders.LotteryDrawViewHolder
import com.example.calotteryapp.presentation.viewholders.SeparatorViewHolder
import java.text.SimpleDateFormat

class LotteryDrawAdapter(
    private val lotteryDraws: List<Any>,         // list type is any because of separators
    private val simpleDateFormat: SimpleDateFormat,
    private val appPreferences: AppPreferences
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 2 -> SEPARATOR_ITEM
            1 -> RECENT_LOTTERY_DRAW
            else -> STANDARD_LOTTERY_DRAW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEPARATOR_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.separator_item, parent, false)
                SeparatorViewHolder(view)
            }
            RECENT_LOTTERY_DRAW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recent_lottery_draw, parent, false)
                LotteryDrawViewHolder(view)
            }
            STANDARD_LOTTERY_DRAW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.standard_lottery_draw, parent, false)
                LotteryDrawViewHolder(view)
            }
            else -> throw IndexOutOfBoundsException("Invalid viewType for $TAG")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = lotteryDraws[position]
        when (holder) {
            is LotteryDrawViewHolder -> {
                holder.bind(
                    data as LotteryDraw,
                    simpleDateFormat,
                    appPreferences
                )
            }
            is SeparatorViewHolder -> {
                holder.bind(data as String)
            }
        }
    }

    override fun getItemCount(): Int {
        return lotteryDraws.size            // add 2 for separators
    }

    companion object {
        private const val SEPARATOR_ITEM = 0
        private const val RECENT_LOTTERY_DRAW = 1
        private const val STANDARD_LOTTERY_DRAW = 2

        private const val TAG = "LotteryDrawAdapter"
    }

}
