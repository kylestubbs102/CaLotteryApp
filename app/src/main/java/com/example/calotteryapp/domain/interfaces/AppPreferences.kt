package com.example.calotteryapp.domain.interfaces

interface AppPreferences {
    fun insertBoolean(key: String, value: Boolean)

    fun insertInt(key: String, value: Int)

    fun insertLong(key: String, value: Long)

    fun insertString(key: String, value: String)

    fun insertList(key: String, value: List<Int>)

    fun getBoolean(key: String): Boolean

    fun getInt(key: String): Int

    fun getLong(key: String): Long

    fun getString(key: String): String?

    fun getList(key: String): List<Int>
}
