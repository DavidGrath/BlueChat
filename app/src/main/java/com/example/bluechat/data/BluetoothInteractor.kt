package com.example.bluechat.data

import androidx.lifecycle.LiveData
import com.example.bluechat.domain.models.AvailableDevice
import com.example.bluechat.utils.SocketCallback

interface BluetoothInteractor {
    fun write(bytes : ByteArray, address : String)
    fun openChatWithDevice(address : String, onFinished : (success : Boolean)->Unit)
    fun getScannedDevices() : LiveData<ArrayList<AvailableDevice>>
    fun getScanState() : LiveData<BluetoothScanState>
    fun getVisibility() : LiveData<BluetoothVisibility>
    fun subscribeForConnectionUpdates(address : String, callback: SocketCallback)
}