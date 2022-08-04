package com.hms.readinghabittracker.service

import android.content.Intent
import com.hms.readinghabittracker.listener.IServiceListener
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService

class AuthenticationService(

    private val authService: HuaweiIdAuthService,
    private val agConnectAuth: AGConnectAuth
) {
    fun getSignedInUser(data: Intent?, serviceListener: IServiceListener<AGConnectUser>) {
        getAuthHuaweiId(data)?.let { authHuaweiId ->
            getSignedInUser(authHuaweiId, serviceListener)
        } ?: kotlin.run {
            serviceListener.onError()
        }
    }

    fun getSignedInUser(
        authHuaweiId: AuthHuaweiId,
        serviceListener: IServiceListener<AGConnectUser>
    ) {
        val credential = HwIdAuthProvider.credentialWithToken(authHuaweiId.accessToken)
        AGConnectAuth.getInstance().signIn(credential)
            .addOnSuccessListener {
                serviceListener.onSuccess(it.user)
            }
            .addOnFailureListener {
                serviceListener.onError()
            }
    }

    private fun getAuthHuaweiId(data: Intent?): AuthHuaweiId? {
        data?.let { signedInUser ->
            HuaweiIdAuthManager.parseAuthResultFromIntent(signedInUser)?.let { task ->
                return if (task.isSuccessful) {
                    task.result
                } else
                    null
            }
        } ?: kotlin.run {
            return null
        }
    }

    fun signOut() {
        agConnectAuth.signOut()
    }

}