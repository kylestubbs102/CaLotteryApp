package com.example.calotteryapp.di

import com.example.calotteryapp.util.DATABASE_DATE_FORMAT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DateModule {
    @Provides
    @Singleton
    fun provideSimpleDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATABASE_DATE_FORMAT, Locale.getDefault())
    }
}
