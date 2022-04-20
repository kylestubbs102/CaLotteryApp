package com.example.calotteryapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [LotteryDrawEntity::class],
    version = 1
)
@TypeConverters(LotteryConverters::class)
abstract class LotteryDatabase : RoomDatabase() {
    abstract val lotteryDao: LotteryDao
}
