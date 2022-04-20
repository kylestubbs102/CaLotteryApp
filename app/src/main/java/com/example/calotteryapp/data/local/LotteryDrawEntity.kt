package com.example.calotteryapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class LotteryDrawEntity(
    @PrimaryKey val id: Int,
    val date: Date,     // make this long to check for equality, avoids repeated and unnecessary API calls
    @ColumnInfo(name = "winning_numbers") val winningNumbers: List<Int>,
    @ColumnInfo(name = "prize_amount") val prizeAmount: Int,
)
