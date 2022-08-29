package com.hms.readinghabittracker.utils

sealed interface Resource<out T> {
    class Success<T>(val data: T) : Resource<T>
    class Error(val exception: Exception) : Resource<Nothing>
    object Loading : Resource<Nothing>
}