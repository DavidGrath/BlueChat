package com.example.bluechat.data.repositories

import androidx.lifecycle.LiveData
import com.example.bluechat.data.BluetoothInteractor
import com.example.bluechat.data.BluetoothScanState
import com.example.bluechat.data.BluetoothVisibility
import com.example.bluechat.domain.models.AvailableDevice

class StartChatRepository(private var interactor: BluetoothInteractor) {
    fun openChatWithDevice(address : String,onFinished : (success : Boolean) -> Unit) {
        interactor.openChatWithDevice(address, onFinished)
    }
    fun getAvailableDevices() : LiveData<ArrayList<AvailableDevice>>{
        return interactor.getScannedDevices()
    }
    fun getVisibility() : LiveData<BluetoothVisibility> {
        return interactor.getVisibility()
    }
    fun getScanState() : LiveData<BluetoothScanState> {
        return interactor.getScanState()
    }
}