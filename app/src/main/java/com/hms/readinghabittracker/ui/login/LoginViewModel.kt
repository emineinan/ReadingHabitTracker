package com.hms.readinghabittracker.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.model.User
import com.hms.readinghabittracker.data.repository.AuthenticationRepository
import com.hms.readinghabittracker.listener.IServiceListener
import com.hms.readinghabittracker.utils.Resource
import com.huawei.agconnect.auth.AGConnectUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState.initial())
    val loginUiState: StateFlow<LoginUiState> get() = _loginUiState.asStateFlow()

    @ExperimentalCoroutinesApi
    fun signInUserAndSaveToCloud(data: Intent?) {
        authenticationRepository.signInUserToAgcConnect(
            data,
            object : IServiceListener<AGConnectUser> {
                override fun onSuccess(successResult: AGConnectUser) {
                    saveUserToCloudDb(successResult)
                }

                override fun onError(exception: Exception) {
                    setErrorState(exception)
                }
            })
    }

    @ExperimentalCoroutinesApi
    fun saveUserToCloudDb(
        agcUser: AGConnectUser
    ) {
        //Check user exist in the Cloud DB
        // if (is user exist in CloudDb(agcUser.uid.toLong)){
        // return
        // }

        val user = User(agcUser.uid.toLong(), agcUser.displayName)

        viewModelScope.launch {
            authenticationRepository.saveUserToCloudDb(user).collect {
                when (it) {
                    is Resource.Error -> setErrorState(it.exception)
                    is Resource.Loading -> setLoadingState()
                    is Resource.Success -> setUserSignedState()
                }
            }
        }
    }

    private fun setErrorState(exception: Exception) {
        _loginUiState.update { currentLoginUiState ->
            val errorMessage =
                currentLoginUiState.error + exception.localizedMessage.orEmpty()
            currentLoginUiState.copy(error = errorMessage, loading = false)
        }
    }

    private fun setLoadingState() {
        _loginUiState.update { currentLoginUiState ->
            currentLoginUiState.copy(loading = true)
        }
    }

    private fun setUserSignedState() {
        _loginUiState.update { currentLoginUiState ->
            val isUserSigned = currentLoginUiState.isUSerSigned + true
            currentLoginUiState.copy(isUSerSigned = isUserSigned, loading = false)
        }
    }
}