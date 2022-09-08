package com.hms.readinghabittracker.ui.mybooks

import com.hms.readinghabittracker.data.model.Collection

data class MyBooksUiState(
    val loading: Boolean,
    val savedMyBookList: List<Collection>,
    val error: List<String>
) {
    companion object {
        fun initial() = MyBooksUiState(
            loading = false,
            savedMyBookList = emptyList(),
            error = emptyList()
        )
    }
}