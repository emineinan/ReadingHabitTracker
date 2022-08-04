package com.hms.readinghabittracker.listener

interface IServiceListener<T> {
    fun onSuccess(successResult: T)
    fun onError()
}