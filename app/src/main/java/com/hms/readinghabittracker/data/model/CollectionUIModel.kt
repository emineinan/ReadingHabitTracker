package com.hms.readinghabittracker.data.model

data class CollectionUIModel(
    val id: Long,
    val name: String,
    val books: List<Book>
)