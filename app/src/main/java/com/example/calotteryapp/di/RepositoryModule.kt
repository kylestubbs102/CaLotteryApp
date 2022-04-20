package com.example.calotteryapp.di

import com.example.calotteryapp.data.local.LotteryDao
import com.example.calotteryapp.data.remote.LotteryApi
import com.example.calotteryapp.data.repository.LotteryRepositoryImpl
import com.example.calotteryapp.domain.repository.LotteryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import java.text.SimpleDateFormat
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideLotteryRepository(
        lotteryDao: LotteryDao,
        lotteryApi: LotteryApi,
        simpleDateFormat: SimpleDateFormat,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): LotteryRepository = LotteryRepositoryImpl(
        lotteryDao,
        lotteryApi,
        simpleDateFormat,
        ioDispatcher
    )

}