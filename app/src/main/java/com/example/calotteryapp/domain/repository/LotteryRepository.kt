package com.example.calotteryapp.domain.repository

import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface LotteryRepository {
    suspend fun updateLotteryResults()

    suspend fun getLotteryResultsInDb(): List<LotteryDraw>
}
