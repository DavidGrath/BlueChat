package com.example.bluechat.framework.services

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bluechat.data.*
import com.example.bluechat.domain.models.AvailableDevice
import com.example.bluechat.domain.models.Chat
import com.example.bluechat.framework.BluetoothDeviceGeneric
import com.example.bluechat.utils.*
import com.example.bluechat.utils.Constants.Companion.RFCOMM_UUID
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainBluetoothService : Service() , BluetoothInteractor{

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var plainHandler : PlainMessageHandler? = null
    val binder = MainBluetoothBinder()
    private var scanState : BluetoothScanState = BluetoothScanState.NotScanning()
    private var _scanStateLiveData = MutableLiveData<BluetoothScanState>(scanState)
    val scanStateLiveData : LiveData<BluetoothScanState> = _scanStateLiveData
    override fun getScanState(): LiveData<BluetoothScanState> {
        return scanStateLiveData
    }
    private var visibility : BluetoothVisibility = BluetoothVisibility.Invisible()
    private var _visibilityLiveData = MutableLiveData<BluetoothVisibility>(visibility)
    val visibilityLiveData : LiveData<BluetoothVisibility> = _visibilityLiveData
    override fun getVisibility(): LiveData<BluetoothVisibility> {
        return visibilityLiveData
    }

    override fun subscribeForMessages(plainHandler: PlainMessageHandler) {
        this.plainHandler = plainHandler
    }

    override fun openChatWithDevice(position: Int, onFinished: (success: Boolean) -> Unit) {
        binder.openChatWithDevice(position, onFinished)
    }

    override fun write(bytes : ByteArray) {
        binder.write(bytes)
    }

    override fun getScannedDevices(): LiveData<ArrayList<AvailableDevice>> {
        return binder.availableDevicesLiveData
    }

    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
    }
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device :BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    binder.addDevice(device)
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED-> {
                    scanState = BluetoothScanState.Scanning()
                    _scanStateLiveData.postValue(scanState)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    scanState = BluetoothScanState.NotScanning()
                    _scanStateLiveData.postValue(scanState)
                }
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE)) {
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                            visibility = BluetoothVisibility.Visible()
                        }
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE, BluetoothAdapter.SCAN_MODE_NONE -> {
                            visibility = BluetoothVisibility.Invisible()
                        }
                    }
                    _visibilityLiveData.postValue(visibility)
                }
            }
        }

    }
    override fun onCreate() {
        super.onCreate()
        registerReceiver(receiver, filter)
        startBluetoothServer()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun startBluetoothServer() {
        Thread {
            bluetoothAdapter?.startDiscovery()
            val server = bluetoothAdapter?.listenUsingRfcommWithServiceRecord("Bluetooth", RFCOMM_UUID)
            var loop = true
            while (loop) {
                Log.d("Server", "Printing")
                val bluetoothSocket: BluetoothSocket? = try {
                    server?.accept()
                } catch (e: IOException) {
                    Log.e("ServerSocket", "Socket's accept() failed", e)
                    e.printStackTrace()
                    null
                }
                bluetoothSocket?.let {
                    binder.socketConnected(it)
//                    binder.socketCallback?.onSocketConnected(it)
                    //Move this to callback implementation
//                    ReadThread(it, binder.handler!!).start()
                }
//                server?.close()
                loop = false
            }
        }.start()
    }

    inner class MainBluetoothBinder() : Binder() {

        var chatPartner : BluetoothDeviceGeneric? = null
        private set
        var socketCallback : SocketCallback? = null
        set(value) {
            chatPartner?.socketCallback = value
            field = value
        }

        val service = this@MainBluetoothService

        private var availableDevices = ArrayList<BluetoothDeviceGeneric>()
        private var availableDevicesPlain = ArrayList<AvailableDevice>()
        private var _availableDevicesLiveData = MutableLiveData<ArrayList<AvailableDevice>>()
        val availableDevicesLiveData : LiveData<ArrayList<AvailableDevice>> = _availableDevicesLiveData

        val bluetoothAdapter : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val thisAddress = bluetoothAdapter?.address

        private var handler: MessageHandler = object : MessageHandler{
            override fun onBluetoothMessage(socket: BluetoothSocket, message: String) {
                plainHandler?.onPlainMessageSent(socket.remoteDevice.address, message)
            }
        }

        fun addDevice(device: BluetoothDevice) {
            if(!availableDevices.any { predicate-> predicate.device.address.equals(device.address)}
                && device.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.PHONE) {
                availableDevices.add(
                    BluetoothDeviceGeneric(
                        device,
                        null,
                        handler,
                        socketCallback
                    )
                )
                availableDevicesPlain.add(AvailableDevice(device.address, device.name))
                _availableDevicesLiveData.value = availableDevicesPlain
            }
        }
        //        var devices = ArrayList<BluetoothDeviceGeneric>()
        fun socketConnected(socket : BluetoothSocket) {
            var device = availableDevices.find {
                    predicate->predicate.device.address.equals(socket.remoteDevice.address)
            }
            if(device == null) {
                device = BluetoothDeviceGeneric(
                    socket.remoteDevice,
                    null,
                    handler,
                    socketCallback
                )
                availableDevices.add(device)
            }
            device.setSocketValue(socket)
        }

        fun openChatWithDevice(position : Int, onFinished: (success: Boolean) -> Unit) {
            Thread {
                var device = availableDevices[position]
                var succeeded = false

                if(device.socket == null) {
                    val bluetoothSocket = device.device.createRfcommSocketToServiceRecord(RFCOMM_UUID)
                    bluetoothAdapter?.cancelDiscovery()
                    try {
                        bluetoothSocket?.connect()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        succeeded = false
                        onFinished.invoke(succeeded)
                        return@Thread
                    }
                    device.setSocketValue(bluetoothSocket)
                }
                chatPartner = device
                succeeded = true
                onFinished.invoke(succeeded)
            }.start()
        }

        fun write(bytes: ByteArray) {
            chatPartner?.sendMessage(bytes)
        }

        fun titleOfCurrentPartnerConnection(): String {
            return chatPartner?.device?.name?: "No Name"
        }
    }
}