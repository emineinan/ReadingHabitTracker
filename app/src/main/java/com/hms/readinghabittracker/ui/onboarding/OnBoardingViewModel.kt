package com.hms.readinghabittracker.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel() {

    fun saveOnBoardingShowedStatus(status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.saveOnBoardingState(status)
        }
    }
}