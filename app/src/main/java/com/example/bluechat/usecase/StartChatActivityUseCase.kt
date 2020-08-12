package com.example.bluechat.usecase

import androidx.lifecycle.LiveData
import com.example.bluechat.data.BluetoothScanState
import com.example.bluechat.data.BluetoothVisibility
import com.example.bluechat.data.repositories.StartChatRepository
import com.example.bluechat.domain.models.AvailableDevice

class StartChatActivityUseCase(var repository : StartChatRepository) : UseCase {
    fun openChatWithDevice(address : String,onFinished : (success : Boolean) -> Unit) {
        repository.openChatWithDevice(address, onFinished)
    }
    fun getAvailableDevices() : LiveData<ArrayList<AvailableDevice>> {
        return repository.getAvailableDevices()
    }
    fun getVisibility() : LiveData<BluetoothVisibility> {
        return repository.getVisibility()
    }
    fun getScanState() : LiveData<BluetoothScanState> {
        return repository.getScanState()
    }
}