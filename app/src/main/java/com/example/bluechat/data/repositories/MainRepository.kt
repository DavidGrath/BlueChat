package com.example.bluechat.data.repositories

import com.example.bluechat.data.BluetoothInteractor

class MainRepository(var interactor: BluetoothInteractor) {
    fun attemptConnectionWithDevice(address : String, onFinished : (success : Boolean)->Unit) {
        interactor.openChatWithDevice(address, onFinished)
    }
}