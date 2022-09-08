package com.example.calotteryapp.presentation.viewholders

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.example.calotteryapp.R
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.util.Constants.CURRENCY_FORMAT
import com.example.calotteryapp.util.Constants.MEGA_USER_NUMBER_PREF_KEY
import com.example.calotteryapp.util.Constants.REGULAR_USER_NUMBERS_PREF_KEY
import java.text.SimpleDateFormat
import java.util.Date

class LotteryDrawViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {
    private var dateTextView: TextView = view.findViewById(R.id.dateTextView)
    private var numbersTextView: TextView = view.findViewById(R.id.numbersTextView)
    private var prizeTextView: TextView = view.findViewById(R.id.prizeTextView)

    fun bind(
        lotteryDraw: LotteryDraw,
        simpleDateFormat: SimpleDateFormat,
        appPreferences: AppPreferences
    ) {
        dateTextView.text = bindDate(
            lotteryDraw.date,
            simpleDateFormat
        )
        numbersTextView.text = bindNumbers(
            lotteryDraw.winningNumbers,
            appPreferences
        )
        prizeTextView.text = bindPrize(lotteryDraw.prizeAmount)
    }


    private fun bindDate(
        date: Date,
        simpleDateFormat: SimpleDateFormat
    ): String {
        return simpleDateFormat.format(date)
    }

    private fun bindNumbers(
        winningNumbers: List<Int>,
        appPreferences: AppPreferences
    ): CharSequence {
        if (winningNumbers.isNullOrEmpty()) return ""

        val regularUserNumbers = appPreferences.getList(REGULAR_USER_NUMBERS_PREF_KEY)
        val megaUserNumber = appPreferences.getInt(MEGA_USER_NUMBER_PREF_KEY)

        val regularWinningsNumbers = winningNumbers.dropLast(1)
        val megaWinningNumber = winningNumbers.last()

        val originalColor = numbersTextView.currentTextColor

        val getTextColor =
            { num: Int, winNums: List<Int> -> if (num in winNums) Color.RED else originalColor }

        var spanBuilder = SpannableStringBuilder()

        for (i in 0..4) {
            spanBuilder = spanBuilder
                .color(getTextColor(regularWinningsNumbers[i], regularUserNumbers)) {
                    append(regularWinningsNumbers[i].toString())
                }
                .append(", ")
        }       // regular numbers

        spanBuilder = spanBuilder
            .color(getTextColor(megaWinningNumber, listOf(megaUserNumber))) {
                append(megaWinningNumber.toString())
            }   // mega number

        return spanBuilder
    }

    private fun bindPrize(prizeAmount: Int): String {
        val prizeAmountString = String.format(CURRENCY_FORMAT, prizeAmount)
        return "$$prizeAmountString"
    }
}