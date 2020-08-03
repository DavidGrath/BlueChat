package com.example.bluechat.utils

import android.bluetooth.BluetoothSocket

interface MessageHandler {
    fun onBluetoothMessage(socket: BluetoothSocket, message: String)
}