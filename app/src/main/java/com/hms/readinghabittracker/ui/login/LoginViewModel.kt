package com.hms.readinghabittracker.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.model.User
import com.hms.readinghabittracker.data.repository.AuthenticationRepository
import com.hms.readinghabittracker.data.repository.CloudDbRepository
import com.hms.readinghabittracker.utils.listener.IServiceListener
import com.hms.readinghabittracker.utils.Resource
import com.huawei.agconnect.auth.AGConnectUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val cloudDbRepository: CloudDbRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState.initial())
    val loginUiState: StateFlow<LoginUiState> get() = _loginUiState.asStateFlow()

    fun signInUserAndSaveToCloud(data: Intent?) {
        authenticationRepository.signInUserToAgcConnect(
            data,
            object : IServiceListener<AGConnectUser> {
                override fun onSuccess(successResult: AGConnectUser) {
                    checkUserByIdAndSaveUserToCloudDbIfNotExist(successResult)
                }

                override fun onError(exception: Exception) {
                    setErrorState(exception)
                }
            })
    }

    fun checkUserByIdAndSaveUserToCloudDbIfNotExist(
        agcUser: AGConnectUser
    ) {
        viewModelScope.launch {
            cloudDbRepository.checkUserById(agcUser.uid.toLong()).collect {
                when (it) {
                    is Resource.Loading -> setLoadingState()
                    else -> {
                        if (it is Resource.Success && it.data) {
                            setUserSignedState()
                        } else {
                            saveUserToCloudDb(agcUser)
                        }
                    }
                }
            }
        }
    }

    private fun saveUserToCloudDb(agcUser: AGConnectUser) {
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