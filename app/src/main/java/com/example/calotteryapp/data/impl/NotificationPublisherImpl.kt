package com.example.calotteryapp.data.impl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.calotteryapp.R
import com.example.calotteryapp.domain.interfaces.NotificationPublisher
import com.example.calotteryapp.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService

class NotificationPublisherImpl(
    private val context: Context
) : NotificationPublisher {

    override fun publishNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        notifyNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = NotificationPublisher.NOTIFICATION_CHANNEL_NAME
        val descriptionText = NotificationPublisher.NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel =
            NotificationChannel(NotificationPublisher.NOTIFICATION_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager =
            context.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
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

        val description = NotificationPublisher.LOTTERY_DESCRIPTION

        val builder =
            NotificationCompat.Builder(context, NotificationPublisher.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(NotificationPublisher.LOTTERY_TITLE)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_wheel)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NotificationPublisher.NOTIFICATION_ID, builder.build())
        }
    }
}