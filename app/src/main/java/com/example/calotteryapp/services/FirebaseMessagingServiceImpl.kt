package com.example.calotteryapp.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.calotteryapp.di.IoDispatcher
import com.example.calotteryapp.domain.interfaces.NotificationPublisher
import com.example.calotteryapp.domain.repository.LotteryRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class FirebaseMessagingServiceImpl : FirebaseMessagingService() {

    @Inject
    lateinit var notificationPublisher: NotificationPublisher

    @Inject
    lateinit var lotteryRepository: LotteryRepository

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    override fun onMessageReceived(message: RemoteMessage) {
        notificationPublisher.publishNotification()
        CoroutineScope(ioDispatcher).launch {
            lotteryRepository.updateLotteryResults()
        }
    }
}