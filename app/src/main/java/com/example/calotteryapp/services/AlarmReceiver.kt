package com.example.calotteryapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.calotteryapp.R
import com.example.calotteryapp.di.IoDispatcher
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.domain.repository.LotteryRepository
import com.example.calotteryapp.presentation.MainActivity
import com.example.calotteryapp.util.DATA_ALREADY_FETCHED
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var lotteryRepository: LotteryRepository

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Broadcast received")
        CoroutineScope(ioDispatcher).launch {
            appPreferences.insertBoolean(DATA_ALREADY_FETCHED, false)
            lotteryRepository.updateLotteryResults()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        notifyNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = NOTIFICATION_CHANNEL_NAME
        val descriptionText = NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun notifyNotification() {
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(LOTTERY_TITLE)
            .setContentText(LOTTERY_DESCRIPTION)
            .setSmallIcon(R.drawable.ic_wheel)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "Lottery"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Lottery"
        const val LOTTERY_TITLE = "Lottery results"
        const val LOTTERY_DESCRIPTION =
            "Your results are ready!" // maybe change it to "4 out of 6 right"
        const val TAG = "AlarmReceiver"
    }
}
