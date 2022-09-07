package com.hms.readinghabittracker.ui.collections

import com.hms.readinghabittracker.data.model.Collection

data class CollectionsUiState(
    val loading: Boolean,
    val isCollectionSaved: List<Boolean>,
    val savedCollectionList: List<Collection>,
    val error: List<String>
) {
    companion object {
        fun initial() = CollectionsUiState(
            loading = false,
            isCollectionSaved = emptyList(),
            savedCollectionList = emptyList(),
            error = emptyList()
        )
    }
}