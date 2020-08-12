package com.example.bluechat.framework

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.bluechat.utils.MessageHandler
import com.example.bluechat.utils.SocketCallback
import com.example.bluechat.utils.SocketConnectionState
import java.io.IOException
import java.io.InputStream

class BluetoothDeviceGeneric(var device: BluetoothDevice, socket: BluetoothSocket?, handler: MessageHandler, var socketCallback: SocketCallback?) {
    private var readThread : ReadThread? = null
    var state : SocketConnectionState =
        SocketConnectionState.Connecting()
    var socket = socket
    fun setSocketValue(value : BluetoothSocket) {
        this.socket = value
        state = SocketConnectionState.Connected()
        socketCallback?.onSocketStateChanged(state)
        startListeningForMessages(handler)
    }
    var handler = handler
    fun sendMessage(bytes : ByteArray/*, onFinished : (success : Boolean) -> Unit*/) {
        Thread {
                var success = false
                try {
                    socket?.outputStream?.write(bytes)
                    success = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("Write Bytes", "Error Occured while sending data")
                } finally {
//                    onFinished.invoke(success)
                }
        }.start()
    }
    fun startListeningForMessages(handler : MessageHandler) {
        if(readThread == null) {
            readThread = ReadThread(handler)
        }
        readThread!!.apply {
            if(!isAlive) start()
        }
    }

    fun socketFailedReadWrite() {
        state = SocketConnectionState.Closed()
        socketCallback?.onSocketStateChanged(state)
        readThread = null
        socket = null
    }
    private inner class ReadThread(var handler: MessageHandler) : Thread() {

        override fun run() {
            val buffer = ByteArray(8)
            var byteCount: Int
            val inStream: InputStream = socket!!.inputStream
            var textBuffer = StringBuffer()
            while (true) {
                byteCount = try {
                    inStream.read(buffer)
                } catch (e: IOException) {
                    e.printStackTrace()
                    socketFailedReadWrite()
                    break
                }
                textBuffer.append(String(buffer, 0, byteCount))
                if(inStream.available() == 0) {
                    handler.onBluetoothMessage(socket!!, textBuffer.toString())
                    textBuffer = StringBuffer()
                }
            }
        }
    }
}