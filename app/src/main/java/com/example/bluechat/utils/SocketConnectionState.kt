package com.example.bluechat.utils

sealed class SocketConnectionState {
    class Connecting : SocketConnectionState()
    class Connected : SocketConnectionState()
    class Closed : SocketConnectionState()
}