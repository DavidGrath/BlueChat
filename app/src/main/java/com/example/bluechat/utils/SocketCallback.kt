package com.example.bluechat.utils

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

interface SocketCallback{
    fun onSocketStateChanged(device: BluetoothDevice)
}