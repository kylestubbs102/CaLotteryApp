package com.example.calotteryapp.domain.interfaces

interface NotificationPublisher {

    fun publishNotification()

    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "Lottery"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Lottery"
        const val LOTTERY_TITLE = "Lottery results"
        const val LOTTERY_DESCRIPTION =
            "Your results are ready!" // maybe change it to "4 out of 6 right"
    }
}