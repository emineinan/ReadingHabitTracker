package com.hms.readinghabittracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import com.hms.readinghabittracker.utils.Constant.NOTIFICATIONS_CHANNEL_ID
import com.hms.readinghabittracker.utils.Constant.NOTIFICATIONS_CHANNEL_NAME
import com.huawei.agconnect.cloud.database.AGConnectCloudDB
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initCloudDb()
        createNotificationChannel()
    }

    private fun initCloudDb() {
        AGConnectCloudDB.initialize(this)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATIONS_CHANNEL_ID,
            NOTIFICATIONS_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                lightColor = Color.GREEN
                enableLights(true)
                description = "Used to show goals"
            }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}