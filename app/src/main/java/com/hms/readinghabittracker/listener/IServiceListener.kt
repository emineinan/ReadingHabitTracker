package com.hms.readinghabittracker.listener

import java.lang.Exception

interface IServiceListener<T> {
    fun onSuccess(successResult: T)
    fun onError(exception: Exception)
}