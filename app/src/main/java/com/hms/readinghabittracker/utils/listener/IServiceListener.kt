package com.hms.readinghabittracker.utils.listener

import java.lang.Exception

interface IServiceListener<T> {
    fun onSuccess(successResult: T)
    fun onError(exception: Exception)
}