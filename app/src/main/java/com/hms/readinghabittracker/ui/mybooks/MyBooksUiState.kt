package com.hms.readinghabittracker.ui.mybooks

import com.hms.readinghabittracker.data.model.CollectionUIModel

data class MyBooksUiState(
    val loading: Boolean,
    val collectionsAndBooks: List<CollectionUIModel>,
    val error: List<String>,
) {
    companion object {
        fun initial() = MyBooksUiState(
            loading = false,
            collectionsAndBooks = emptyList(),
            error = emptyList()
        )
    }
}