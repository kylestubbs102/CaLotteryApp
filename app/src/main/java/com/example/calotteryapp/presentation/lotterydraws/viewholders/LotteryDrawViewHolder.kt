package com.example.calotteryapp.presentation.lotterydraws.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calotteryapp.R
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.util.CURRENCY_FORMAT
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LotteryDrawViewHolder @Inject constructor(
    view: View
) : RecyclerView.ViewHolder(view) {
    private var dateTextView: TextView = view.findViewById(R.id.dateTextView)
    private var numbersTextView: TextView = view.findViewById(R.id.numbersTextView)
    private var prizeTextView: TextView = view.findViewById(R.id.prizeTextView)

    fun bind(
        lotteryDraw: LotteryDraw,
        simpleDateFormat: SimpleDateFormat
    ) {
        dateTextView.text = bindDate(
            lotteryDraw.date,
            simpleDateFormat
        )
        numbersTextView.text = bindNumbers(lotteryDraw.winningNumbers)
        prizeTextView.text = bindPrize(lotteryDraw.prizeAmount)
    }


    private fun bindDate(
        date: Date,
        simpleDateFormat: SimpleDateFormat
    ): String {
        return simpleDateFormat.format(date)
    }

    private fun bindNumbers(winningNumbers: List<Int>): String {
        return winningNumbers.joinToString(", ").removeSuffix(",")
    }

    private fun bindPrize(prizeAmount: Int): String {
        val prizeAmountString = String.format(CURRENCY_FORMAT, prizeAmount)
        return "$$prizeAmountString"
    }
}