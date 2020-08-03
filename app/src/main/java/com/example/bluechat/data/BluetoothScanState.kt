package com.example.bluechat.data

sealed class BluetoothScanState {
    class Scanning : BluetoothScanState()
    class NotScanning : BluetoothScanState()
}