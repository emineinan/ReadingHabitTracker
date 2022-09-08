package com.hms.readinghabittracker.ui.mybooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.model.Book
import com.hms.readinghabittracker.data.model.CollectionUIModel
import com.hms.readinghabittracker.data.repository.CloudDbRepository
import com.hms.readinghabittracker.ui.collections.CollectionsUiState
import com.hms.readinghabittracker.utils.Resource
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBooksViewModel @Inject constructor(
    private val cloudDbRepository: CloudDbRepository,
    agcUser: AGConnectAuth
) :
    ViewModel() {

    private val _myBooksUiState = MutableStateFlow(MyBooksUiState.initial())
    val myBooksUiState: StateFlow<MyBooksUiState> get() = _myBooksUiState.asStateFlow()

    private val collectionsList = mutableListOf<CollectionUIModel>()

    private val currentUserBooks =
        cloudDbRepository.queryAllBooksForCurrentUser(agcUser.currentUser.uid.toLong())
    private val currentUserCollections =
        cloudDbRepository.queryAllCollectionsForCurrentUser(agcUser.currentUser.uid.toLong())

    init {
        getCollections()
    }

    private fun getCollections(): MutableList<CollectionUIModel> {
        viewModelScope.launch {
          currentUserCollections.collect{
           when(it){
               is Resource.Loading -> setLoadingState()
               is Resource.Error -> setErrorState(it.exception)
               is Resource.Success -> getMyBooks(currentUserBooks)
           }
          }
        }

        return collectionsList
    }

    private fun getMyBooks(currentUserBooks: Flow<Resource<List<Book>>>) {
       viewModelScope.launch {
           currentUserBooks.collect {
               when (it) {
                   is Resource.Error -> setErrorState(it.exception)
                   is Resource.Loading -> setLoadingState()
                   is Resource.Success -> setSavedMyBooksState()
               }
           }
       }
    }

    private fun setSavedMyBooksState() {
        currentUserCollections.zip(currentUserBooks) { collections, books ->
          //Filter operations
        }


        _myBooksUiState.update { currentMyBooksUiState ->
            currentMyBooksUiState.copy(savedMyBookList = currentMyBooksUiState.savedMyBookList)
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

