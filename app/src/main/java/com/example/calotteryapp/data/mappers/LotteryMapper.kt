package com.example.calotteryapp.data.mappers

import com.example.calotteryapp.data.local.LotteryDrawEntity
import com.example.calotteryapp.domain.model.LotteryDraw

fun LotteryDraw.toLotteryDrawEntity(): LotteryDrawEntity =
    LotteryDrawEntity(
        id = id,
        date = date,
        winningNumbers = winningNumbers,
        prizeAmount = prizeAmount,
    )

fun LotteryDrawEntity.toLotteryDraw(): LotteryDraw =
    LotteryDraw(
        id = id,
        date = date,
        winningNumbers = winningNumbers,
        prizeAmount = prizeAmount,
    )