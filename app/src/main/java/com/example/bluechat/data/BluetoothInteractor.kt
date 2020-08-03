package com.example.bluechat.data

import androidx.lifecycle.LiveData
import com.example.bluechat.domain.models.AvailableDevice

interface BluetoothInteractor {
    fun write(bytes : ByteArray)
    fun subscribeForMessages(plainHandler: PlainMessageHandler)
    fun openChatWithDevice(position : Int, onFinished : (success : Boolean)->Unit)
    fun getScannedDevices() : LiveData<ArrayList<AvailableDevice>>
    fun getScanState() : LiveData<BluetoothScanState>
    fun getVisibility() : LiveData<BluetoothVisibility>
}