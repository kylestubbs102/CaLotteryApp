package com.example.calotteryapp.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.*

class LotteryConverters {
    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun longToDate(long: Long): Date {
        return Date(long)
    }

    @TypeConverter
    fun stringToList(string: String): List<Int> {
        return string
            .split(" ")
            .map { it.toInt() }
    }

    @TypeConverter
    fun listToString(list: List<Int>): String {
        return list.joinToString(" ")
    }
}
