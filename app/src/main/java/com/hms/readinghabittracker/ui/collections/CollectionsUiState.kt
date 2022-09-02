package com.hms.readinghabittracker.ui.collections

data class CollectionsUiState(
    val loading: Boolean,
    val isCollectionSaved: List<Boolean>,
    val error: List<String>
) {
    companion object {
        fun initial() = CollectionsUiState(
            loading = false,
            isCollectionSaved = emptyList(),
            error = emptyList()
        )
    }
}