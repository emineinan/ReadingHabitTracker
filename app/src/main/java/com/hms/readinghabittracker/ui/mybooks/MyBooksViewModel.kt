package com.hms.readinghabittracker.ui.mybooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.model.CollectionUIModel
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
class MyBooksViewModel @Inject constructor(
    private val cloudDbRepository: CloudDbRepository,
    agcUser: AGConnectAuth,
) :
    ViewModel() {

    private val userId = agcUser.currentUser.uid.toLong()
    private val _myBooksUiState = MutableStateFlow(MyBooksUiState.initial())
    val myBooksUiState: StateFlow<MyBooksUiState> get() = _myBooksUiState.asStateFlow()

    private val collectionsList = mutableListOf<CollectionUIModel>()

    private val currentUserBooks =
        cloudDbRepository.queryAllBooksForCurrentUser(agcUser.currentUser.uid.toLong())
    private val currentUserCollections =
        cloudDbRepository.queryAllCollectionsForCurrentUser(agcUser.currentUser.uid.toLong())


    private fun getCollectionsForCurrentUser() {
        viewModelScope.launch {
            cloudDbRepository.getCollectionsForCurrentUser(userId).collect {
                when (it) {
                    is Resource.Error -> setErrorState(it.exception)
                    is Resource.Loading -> setLoadingState()
                    is Resource.Success -> {
                        setCollectionsWithBooks(it.data)
                    }
                }
            }
        }
    }

    private fun setCollectionsWithBooks(collectionsAndBooks: List<CollectionUIModel>) {
        _myBooksUiState.update {
            it.copy(
                loading = false,
                error = emptyList(),
                collectionsAndBooks = collectionsAndBooks
            )
        }
    }

    private fun setErrorState(exception: Exception) {
        _myBooksUiState.update { currentMyBooksUiState ->
            val errorMessage =
                currentMyBooksUiState.error + exception.localizedMessage.orEmpty()
            currentMyBooksUiState.copy(error = errorMessage, loading = false)
        }
    }

    private fun setLoadingState() {
        _myBooksUiState.update { currentMyBooksUiState ->
            currentMyBooksUiState.copy(loading = true)
        }
    }
}

