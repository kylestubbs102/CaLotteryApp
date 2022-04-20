package com.example.calotteryapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface LotteryDao {
    @Query("SELECT * FROM LotteryDrawEntity ORDER BY id DESC")
    fun getLotteryDraws(): List<LotteryDrawEntity>?

    @Query("SELECT * FROM LotteryDrawEntity WHERE date = :date")
    fun getLotteryDrawByDate(date: Date): LotteryDrawEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLotteryDraw(lotteryDraw: LotteryDrawEntity)
}
