package com.hms.readinghabittracker.data.repository

import android.content.Intent
import com.hms.readinghabittracker.data.model.User
import com.hms.readinghabittracker.listener.IServiceListener
import com.hms.readinghabittracker.utils.Resource
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor(
    private val agConnectAuth: AGConnectAuth,
    private val cloudDbRepository: CloudDbRepository,
) {
    fun signInUserToAgcConnect(data: Intent?, serviceListener: IServiceListener<AGConnectUser>) {
        getAuthHuaweiIdFromIntent(data)?.let { authHuaweiId ->
            val credential = HwIdAuthProvider.credentialWithToken(authHuaweiId.accessToken)
            agConnectAuth.signIn(credential)
                .addOnSuccessListener {
                    serviceListener.onSuccess(it.user)
                }
                .addOnFailureListener {
                    serviceListener.onError(it)
                }
        } ?: run {
            serviceListener.onError(Exception("Unexpected failure happen"))
        }
    }

    private fun getAuthHuaweiIdFromIntent(data: Intent?): AuthHuaweiId? {
        data?.let { signedInUser ->
            HuaweiIdAuthManager.parseAuthResultFromIntent(signedInUser)?.let { task ->
                return if (task.isSuccessful)
                    task.result
                else
                    null
            }
        } ?: run {
            return null
        }
    }

    fun saveUserToCloudDb(user: User): Flow<Resource<Boolean>> {
        return cloudDbRepository.saveToCloudDb(user)
    }
}