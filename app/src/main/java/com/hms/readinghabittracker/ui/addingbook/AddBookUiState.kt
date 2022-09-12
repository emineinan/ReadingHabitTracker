package com.hms.readinghabittracker.ui.addingbook

import com.hms.readinghabittracker.data.model.Book

data class AddBookUiState(
    val loading: Boolean,
    val isBookSaved: List<Boolean>,
    val savedBookList: List<Book>,
    val error: List<String>
) {
    companion object {
        fun initial() = AddBookUiState(
            loading = false,
            isBookSaved = emptyList(),
            savedBookList = emptyList(),
            error = emptyList()
        )
    }
}