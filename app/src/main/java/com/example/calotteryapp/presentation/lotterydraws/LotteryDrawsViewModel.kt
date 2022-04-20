package com.example.calotteryapp.presentation.lotterydraws

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calotteryapp.di.IoDispatcher
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.domain.repository.LotteryRepository
import com.example.calotteryapp.util.DATA_ALREADY_FETCHED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LotteryDrawsViewModel @Inject constructor(
    private val lotteryRepository: LotteryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _lotteryDraws = MutableLiveData(listOf<LotteryDraw>())
    val lotteryDraws: LiveData<List<LotteryDraw>> = _lotteryDraws

    fun loadLotteryDraws() {
        viewModelScope.launch {
            val dbLotteryResults = withContext(ioDispatcher) {
                if (!appPreferences.getBoolean(DATA_ALREADY_FETCHED)) {     // set to true after call here and on notification
                    _isLoading.postValue(true)
                    lotteryRepository.updateLotteryResults()
                    appPreferences.insertBoolean(DATA_ALREADY_FETCHED, true)
                }
                lotteryRepository.getLotteryResultsInDb()
            }
            _lotteryDraws.postValue(dbLotteryResults)
            _isLoading.postValue(false)
        }
    }
}