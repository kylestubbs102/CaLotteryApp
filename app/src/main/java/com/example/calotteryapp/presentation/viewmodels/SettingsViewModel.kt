package com.example.calotteryapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.util.USER_NUMBERS_PREF_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {
    val userNumbers = MutableLiveData(List(6) { "" })

    private val _resultSuccess = MutableLiveData(false)
    val resultSuccess: LiveData<Boolean> = _resultSuccess

    fun verifyUserNumbers() {
        if (userNumbers.value?.contains("") == true) return

        val lastNum = userNumbers.value?.last()?.toInt()
        val firstNums = userNumbers.value?.dropLast(1)?.map { it.toInt() }

        if (lastNum in 1..27 && firstNums?.all { it in 1..47 } == true) {
            userNumbers.value
                ?.map { it.toInt() }
                ?.let { appPreferences.insertList(USER_NUMBERS_PREF_KEY, it) }

            _resultSuccess.postValue(true)
        } else {
            _resultSuccess.postValue(false)
        }
    }

    fun clearUserNumbers() {
        userNumbers.postValue(List(6) { "" })
    }
}