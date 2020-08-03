package com.example.bluechat.data

sealed class BluetoothVisibility {
    class Visible : BluetoothVisibility()
    class Invisible : BluetoothVisibility()
}