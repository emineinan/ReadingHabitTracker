package com.hms.readinghabittracker.di

import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.cloud.database.AGConnectCloudDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudDBModule {

    @Singleton
    @Provides
    fun provideAGConnectCloudDb(
        agConnectInstance: AGConnectInstance,
        agConnectAuth: AGConnectAuth
    ): AGConnectCloudDB {
        return AGConnectCloudDB.getInstance(
            agConnectInstance,
            agConnectAuth
        )
    }
}