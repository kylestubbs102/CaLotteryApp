package com.example.calotteryapp.di

import android.app.AlarmManager
import android.content.Context
import android.net.ConnectivityManager
import com.example.calotteryapp.data.impl.NotificationPublisherImpl
import com.example.calotteryapp.domain.interfaces.NotificationPublisher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun provideConnectionManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun provideNotificationPublisher(@ApplicationContext context: Context): NotificationPublisher =
        NotificationPublisherImpl(context)
}
