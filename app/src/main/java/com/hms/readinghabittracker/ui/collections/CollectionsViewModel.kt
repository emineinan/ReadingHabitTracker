package com.hms.readinghabittracker.ui.collections

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.model.Collection
import com.hms.readinghabittracker.data.model.User
import com.hms.readinghabittracker.data.repository.CloudDbRepository
import com.hms.readinghabittracker.utils.Resource
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(private val cloudDbRepository: CloudDbRepository) :
    ViewModel() {

    private val _collectionsUiState = MutableStateFlow(CollectionsUiState.initial())
    val collectionsUiState: StateFlow<CollectionsUiState> get() = _collectionsUiState.asStateFlow()

    init {
        getCollections()
    }

    fun saveCollectionToCloudDb(agcUser: AGConnectAuth, collectionName: String) {
        val id = System.currentTimeMillis()
        val collection = Collection(id, collectionName, agcUser.currentUser.uid.toLong())

        viewModelScope.launch {
            cloudDbRepository.saveToCloudDb(collection).collect {
                when (it) {
                    is Resource.Error -> setErrorState(it.exception)
                    is Resource.Loading -> setLoadingState()
                    is Resource.Success -> setSavedCollectionState()
                }
            }
        }
    }

    private fun getCollections() {
        cloudDbRepository.queryAllCollections()
        viewModelScope.launch {
            cloudDbRepository.cloudDbCollectionResponse.collect {
                if(it.isNotEmpty()){
                    _collectionsUiState.update { currentCollectionsUiState ->
                        currentCollectionsUiState.copy(savedCollectionList = it)
                    }
                }

            }
        }
    }

    private fun setErrorState(exception: Exception) {
        _collectionsUiState.update { currentCollectionsUiState ->
            val errorMessage =
                currentCollectionsUiState.error + exception.localizedMessage.orEmpty()
            currentCollectionsUiState.copy(error = errorMessage, loading = false)
        }
    }

    private fun setLoadingState() {
        _collectionsUiState.update { currentCollectionsUiState ->
            currentCollectionsUiState.copy(loading = true)
        }
    }

    private fun setSavedCollectionState() {
        _collectionsUiState.update { currentCollectionsUiState ->
            val isCollectionSaved = currentCollectionsUiState.isCollectionSaved + true
            currentCollectionsUiState.copy(isCollectionSaved = isCollectionSaved, loading = false)
        }
    }
}