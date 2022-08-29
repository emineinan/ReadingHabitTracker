package com.hms.readinghabittracker.di

import android.content.Context
import com.huawei.agconnect.AGCRoutePolicy
import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.cloud.database.AGConnectCloudDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun provideAGConnectInstance(
        @ApplicationContext context: Context
    ): AGConnectInstance {
        val agcConnectOptions =
            AGConnectOptionsBuilder().setRoutePolicy(AGCRoutePolicy.GERMANY).build(context)
        return AGConnectInstance.buildInstance(agcConnectOptions)
    }


    @Singleton
    @Provides
    fun provideAGConnectAuth(
        agConnectInstance: AGConnectInstance
    ): AGConnectAuth {
        return AGConnectAuth.getInstance(agConnectInstance)
    }
}