package com.example.calotteryapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.calotteryapp.data.impl.AppPreferencesImpl
import com.example.calotteryapp.domain.interfaces.AppPreferences
import com.example.calotteryapp.util.SHARED_PREF_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideAppPreferences(
        sharedPreferences: SharedPreferences
    ): AppPreferences {
        return AppPreferencesImpl(sharedPreferences)
    }
}
