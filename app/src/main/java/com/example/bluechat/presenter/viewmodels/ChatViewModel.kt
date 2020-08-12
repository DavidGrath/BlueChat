package com.example.bluechat.presenter.viewmodels

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bluechat.domain.models.Chat
import com.example.bluechat.data.DataProvider
import com.example.bluechat.data.DataProviderImpl
import com.example.bluechat.data.repositories.ChatRepository
import com.example.bluechat.usecase.ChatActivityUseCase
import com.example.bluechat.utils.SocketCallback
import com.example.bluechat.utils.SocketConnectionState

class ChatViewModel(partnerAddress : String,var useCase: ChatActivityUseCase) : ViewModel(){

    val chatsLiveData = useCase.getChatsFromPartner(partnerAddress)

    private var connectionTitle = "Not Connected"
    private val _connectionTitleLiveData = MutableLiveData<String>(connectionTitle)
    val connectionTitleLiveData : LiveData<String> = _connectionTitleLiveData

    init {
        useCase.subscribeToSocketState(partnerAddress, object : SocketCallback {
            override fun onSocketStateChanged(state: SocketConnectionState) {
                connectionStateChanged(state)
            }
        })
    }

    fun connectionStateChanged(state : SocketConnectionState) {
        when (state) {
            is SocketConnectionState.Connected -> {
                connectionTitle = "Connected"
                _connectionTitleLiveData.postValue(connectionTitle)
            }
            is SocketConnectionState.Closed -> {
                connectionTitle = "Not Connected"
                _connectionTitleLiveData.postValue(connectionTitle)
            }
        }
    }
    fun sendMessage(message : String, address : String, deviceName : String?) {
        useCase.sendMessage(message, address, deviceName)
    }

}