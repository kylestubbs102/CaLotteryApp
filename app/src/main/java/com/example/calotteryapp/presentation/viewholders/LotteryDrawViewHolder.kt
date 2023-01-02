package com.example.calotteryapp.presentation.viewholders

import android.graphics.Color
import android.text.SpannableStringBuilder
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.calotteryapp.R
import com.example.calotteryapp.databinding.RecentLotteryDrawBinding
import com.example.calotteryapp.databinding.StandardLotteryDrawBinding
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.domain.interfaces.AppPreferences
import com.example.calotteryapp.util.CURRENCY_FORMAT
import com.example.calotteryapp.util.DATE_PICKER_PREF_KEY
import com.example.calotteryapp.util.MEGA_USER_NUMBER_PREF_KEY
import com.example.calotteryapp.util.REGULAR_USER_NUMBERS_PREF_KEY
import java.text.SimpleDateFormat
import java.util.*

class LotteryDrawViewHolder<T : ViewBinding>(
    private val binding: T
) : RecyclerView.ViewHolder(binding.root) {

    // even though it's the same, we need to do this so that the binding
    // can find the view
    private val originalColor = when (binding) {
        is RecentLotteryDrawBinding -> binding.textviewNumbers.currentTextColor
        is StandardLotteryDrawBinding -> binding.textviewNumbers.currentTextColor
        else -> 0
    }

    fun bind(
        lotteryDraw: LotteryDraw,
        simpleDateFormat: SimpleDateFormat,
        appPreferences: AppPreferences
    ) {
        when (binding) {
            is StandardLotteryDrawBinding -> {
                binding.textviewDate.text = bindDate(
                    lotteryDraw.date,
                    simpleDateFormat
                )
                binding.textviewNumbers.text = bindNumbers(
                    lotteryDraw.winningNumbers,
                    appPreferences
                )
                binding.textviewPrize.text = bindPrize(lotteryDraw.prizeAmount)
            }
            is RecentLotteryDrawBinding -> {
                binding.textviewDate.text = bindDate(
                    lotteryDraw.date,
                    simpleDateFormat
                )
                binding.textviewNumbers.text = bindNumbers(
                    lotteryDraw.winningNumbers,
                    appPreferences
                )
                binding.textviewPrize.text = bindPrize(lotteryDraw.prizeAmount)
                binding.textviewNextLottoDate.text = bindNextLottoDraw(
                    simpleDateFormat,
                    appPreferences
                )
            }
        }
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

        val getNumberTextColor =
            { num: Int, winNums: List<Int> -> if (num in winNums) Color.RED else originalColor }

        val spanBuilder = SpannableStringBuilder()

        for (i in 0..4) {
            spanBuilder
                .color(getNumberTextColor(regularWinningsNumbers[i], regularUserNumbers)) {
                    append(regularWinningsNumbers[i].toString())
                }
                .append(", ")
        }       // regular numbers

        spanBuilder
            .color(getNumberTextColor(megaWinningNumber, listOf(megaUserNumber))) {
                append(megaWinningNumber.toString())
            }   // mega number

        return spanBuilder
    }

    private fun bindPrize(prizeAmount: Int): String {
        val prizeAmountString = String.format(CURRENCY_FORMAT, prizeAmount)
        return "$$prizeAmountString"
    }

    private fun bindNextLottoDraw(
        simpleDateFormat: SimpleDateFormat,
        appPreferences: AppPreferences
    ): CharSequence {
        val nextLottoDraw = appPreferences.getLong(DATE_PICKER_PREF_KEY)

        if (nextLottoDraw == 0L) {      // no lotto date set
            return binding.root.context.getString(R.string.lottery_buy_date_not_set)
        }

        val nextLottoDrawDate = Date(nextLottoDraw)
        val nextLottoDrawDateString = simpleDateFormat.format(nextLottoDrawDate)

        val currentDateString = simpleDateFormat.format(Date())
        val currentDate = simpleDateFormat.parse(currentDateString)

        val textColor = if (nextLottoDrawDate.before(currentDate)) {
            Color.RED
        } else {
            originalColor
        }

        return SpannableStringBuilder()
            .append(binding.root.context.getString(R.string.lottery_buy_date))
            .color(textColor) { append(nextLottoDrawDateString) }
    }
}