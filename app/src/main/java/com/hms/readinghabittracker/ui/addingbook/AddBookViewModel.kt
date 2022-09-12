package com.hms.readinghabittracker.ui.addingbook

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.model.Book
import com.hms.readinghabittracker.data.repository.CloudDbRepository
import com.hms.readinghabittracker.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val cloudDbRepository: CloudDbRepository,
) : ViewModel() {
    private val _addBookUiState = MutableStateFlow(AddBookUiState.initial())
    val addBookUiState get() = _addBookUiState.asStateFlow()

    private val _selectedBitmap = MutableLiveData<Bitmap>()
    val selectedBitmap: LiveData<Bitmap> = _selectedBitmap


    fun setSelectedBitmap(bitmap: Bitmap) {
        _selectedBitmap.value = bitmap
    }

    fun saveBookToCloudDb(
        title: String,
        author: String,
        pages: Int,
        image: ByteArray,
        userId: Long,
        collectionId: Long
    ) {
        val id = System.currentTimeMillis()
        val book = Book(
            id,
            title,
            author,
            pages,
            image,
            userId,
            collectionId
        )
        viewModelScope.launch {
            cloudDbRepository.saveToCloudDb(book).collect {
                when (it) {
                    is Resource.Error -> setErrorState(it.exception)
                    is Resource.Loading -> setLoadingState()
                    is Resource.Success -> setSavedBookState()
                }
            }
        }
    }

    private fun setErrorState(exception: Exception) {
        _addBookUiState.update { currentBookUiState ->
            val errorMessage =
                currentBookUiState.error + exception.localizedMessage.orEmpty()
            currentBookUiState.copy(error = errorMessage, loading = false)
        }
    }

    private fun setLoadingState() {
        _addBookUiState.update { currentBookUiState ->
            currentBookUiState.copy(loading = true)
        }
    }

    private fun setSavedBookState() {
        _addBookUiState.update { currentBookUiState ->
            val isBookSaved = currentBookUiState.isBookSaved + true
            currentBookUiState.copy(isBookSaved = isBookSaved, loading = false)
        }
    }
}