package com.online.coursestore.manager.listener

interface MapCallback<T, U> {
    fun onMapReceived(map: Map<T, U>)
}