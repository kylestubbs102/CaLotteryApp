package com.example.calotteryapp.domain.model

import java.util.*

data class LotteryDraw(
    val id: Int = -1,
    val date: Date = Date(),
    val winningNumbers: List<Int> = listOf(),
    val prizeAmount: Int = 0,
)
