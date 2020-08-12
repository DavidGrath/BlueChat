package com.example.bluechat.utils

interface SocketCallback{
    fun onSocketStateChanged(state : SocketConnectionState)
}