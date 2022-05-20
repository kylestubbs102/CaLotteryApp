package com.example.calotteryapp.domain.repository

import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.util.Resource

interface LotteryRepository {
    suspend fun updateLotteryResults(): Resource<*>

    suspend fun getLotteryResultsInDb(): List<LotteryDraw>
}
