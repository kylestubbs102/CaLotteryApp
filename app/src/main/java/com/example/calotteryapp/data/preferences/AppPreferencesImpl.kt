package com.example.calotteryapp.data.preferences

import android.content.SharedPreferences
import com.example.calotteryapp.domain.preferences.AppPreferences

class AppPreferencesImpl(
    private val sharedPreferences: SharedPreferences
) : AppPreferences {
    override fun insertBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun insertInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    override fun insertLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun insertString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun insertList(key: String, value: List<Int>) {
        val valueToStr = value.joinToString(" ")
        sharedPreferences.edit().putString(key, valueToStr).apply()
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    override fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, 0)
    }

    override fun getString(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    override fun getList(key: String): List<Int> {
        val listStr = sharedPreferences.getString(key, "")

        // return empty list if default value
        return if (listStr.isNullOrEmpty()) {
            listOf()
        } else {
            listStr
                .split(" ")
                .map { it.toInt() }
        }
    }
}
