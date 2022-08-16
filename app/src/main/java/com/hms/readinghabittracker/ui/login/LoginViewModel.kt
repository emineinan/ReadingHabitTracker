package com.hms.readinghabittracker.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.hms.readinghabittracker.listener.IServiceListener
import com.hms.readinghabittracker.service.AuthenticationService
import com.huawei.agconnect.auth.AGConnectUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authenticationService: AuthenticationService) :
    ViewModel() {

    private var agConnectUser: AGConnectUser? = null

    fun userSignedIn(data: Intent?) {
        authenticationService.getSignedInUser(data, object : IServiceListener<AGConnectUser> {
            override fun onSuccess(successResult: AGConnectUser) {
                agConnectUser = successResult
            }

            override fun onError() {
                agConnectUser = null
            }
        })
    }
}