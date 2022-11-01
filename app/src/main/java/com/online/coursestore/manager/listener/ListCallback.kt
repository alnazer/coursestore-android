package com.online.coursestore.manager.listener

interface ListCallback<T> {
    fun onMapReceived(items: List<T>)
}