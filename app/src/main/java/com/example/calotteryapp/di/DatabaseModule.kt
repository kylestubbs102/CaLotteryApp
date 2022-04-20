package com.example.calotteryapp.di

import android.app.Application
import androidx.room.Room
import com.example.calotteryapp.data.local.LotteryConverters
import com.example.calotteryapp.data.local.LotteryDao
import com.example.calotteryapp.data.local.LotteryDatabase
import com.example.calotteryapp.util.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideLotteryDatabase(
        app: Application
    ): LotteryDatabase {
        return Room.databaseBuilder(
            app,
            LotteryDatabase::class.java,
            DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideLotteryDao(db: LotteryDatabase): LotteryDao = db.lotteryDao

}
