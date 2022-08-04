package com.hms.readinghabittracker.ui.splash

import androidx.lifecycle.ViewModel
import com.hms.readinghabittracker.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(dataStoreManager: DataStoreManager) : ViewModel() {
    val isOnBoardingShowedFlow = dataStoreManager.isOnBoardingShowed
}