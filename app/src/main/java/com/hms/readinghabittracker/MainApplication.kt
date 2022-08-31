package com.hms.readinghabittracker

import android.app.Application
import com.huawei.agconnect.cloud.database.AGConnectCloudDB
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initCloudDb()
    }

    private fun initCloudDb() {
        AGConnectCloudDB.initialize(this)
    }
}