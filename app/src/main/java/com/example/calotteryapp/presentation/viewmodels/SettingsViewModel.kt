package com.example.calotteryapp.presentation.viewmodels

import android.os.Build
import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.util.Constants.DATE_PICKER_PREF_KEY
import com.example.calotteryapp.util.Constants.MEGA_USER_NUMBER_PREF_KEY
import com.example.calotteryapp.util.Constants.REGULAR_USER_NUMBERS_PREF_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
) : ViewModel() {
    val userNumbers = MutableLiveData(List(6) { 1 })

    val day = MutableLiveData(0)
    val month = MutableLiveData(0)
    val year = MutableLiveData(0)

    private val _resultSuccess = MutableLiveData(false)
    val resultSuccess: LiveData<Boolean> = _resultSuccess

    fun fetchUserSettings() {
        val regularNumbers = appPreferences.getList(REGULAR_USER_NUMBERS_PREF_KEY)
        val megaNumber = appPreferences.getInt(MEGA_USER_NUMBER_PREF_KEY)

        if (regularNumbers.isNullOrEmpty()) {
            userNumbers.postValue(List(6) { 1 })
        } else {
            userNumbers.postValue(regularNumbers + megaNumber)
        }

        val lottoBuyDate = appPreferences.getLong(DATE_PICKER_PREF_KEY)

        if (lottoBuyDate == 0L) {
            val date = Date()

            val d = DateFormat.format("dd", date) as String
            day.postValue(d.toInt())

            val monthNumber = DateFormat.format("MM", date) as String
            month.postValue(monthNumber.toInt() - 1)

            val y = DateFormat.format("yyyy", date) as String
            year.postValue(y.toInt())
        } else {
            val dateString: String = DateFormat.format("MM/dd/yyyy", Date(lottoBuyDate)).toString()
            val (m, d, y) = dateString
                .split("/")
                .map { it.toInt() }

            day.postValue(d)
            month.postValue(m - 1) // need to account for month index starting at 0
            year.postValue(y)
        }
    }

    fun verifyUserNumbers() {
        val firstNums = userNumbers.value?.dropLast(1)
        val lastNum = userNumbers.value?.last()?.toInt()

        val inputDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Calendar.Builder().setDate(year.value!!, month.value!!, day.value!!)
                .build().time
        } else {
            Date(year.value!!, month.value!!, day.value!!)
        }

        if (numbersAreDistinct(firstNums) && inputDateIsValid(inputDate)) {
            firstNums
                ?.sorted()
                ?.let { appPreferences.insertList(REGULAR_USER_NUMBERS_PREF_KEY, it) }

            lastNum?.let { appPreferences.insertInt(MEGA_USER_NUMBER_PREF_KEY, it) }

            appPreferences.insertLong(DATE_PICKER_PREF_KEY, inputDate.time)

            _resultSuccess.postValue(true)
        } else {
            _resultSuccess.postValue(false)
        }
    }

    private fun numbersAreDistinct(firstNums: List<Int>?): Boolean =
        firstNums?.distinct()?.toTypedArray() contentEquals firstNums?.toTypedArray()

    // checks if date is tomorrow or later
    private fun inputDateIsValid(inputDate: Date): Boolean {
        val todaysDate = Date()
        return inputDate.after(todaysDate)
    }
}