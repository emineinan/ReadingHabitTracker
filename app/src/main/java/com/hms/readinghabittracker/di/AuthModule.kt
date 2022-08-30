package com.hms.readinghabittracker.di

import android.content.Context
import com.huawei.agconnect.AGCRoutePolicy
import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    @Singleton
    fun provideHuaweiIdAuthParams(): HuaweiIdAuthParams {
        return HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setAccessToken()
            .createParams()
    }

    @Provides
    @Singleton
    fun provideHuaweiIdAuthService(
        @ApplicationContext context: Context,
        huaweiIdAuthParams: HuaweiIdAuthParams
    ): HuaweiIdAuthService {
        return HuaweiIdAuthManager.getService(context, huaweiIdAuthParams)
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