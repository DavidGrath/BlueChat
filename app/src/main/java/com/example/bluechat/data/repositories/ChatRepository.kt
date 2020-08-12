package com.example.bluechat.data.repositories

import androidx.lifecycle.LiveData
import com.example.bluechat.data.BluetoothInteractor
import com.example.bluechat.data.DataProvider
import com.example.bluechat.data.PlainMessageHandler
import com.example.bluechat.domain.models.Chat
import com.example.bluechat.utils.Constants.Companion.OWN_ADDRESS
import com.example.bluechat.utils.SocketCallback
import java.util.*
import kotlin.collections.ArrayList

class ChatRepository(private var provider : DataProvider,private var interactor: BluetoothInteractor) {

    fun getChatsFromPartner(partnerAddress : String) : LiveData<ArrayList<Chat>>{
        return provider.getChatsFromPartner(partnerAddress)
    }


    fun sendMessage(message : String, address: String, deviceName : String?) {
        try{
            interactor.write(message.toByteArray(), address)
            provider.addChat(Chat(message, Date().time, "02:00:00:00:00:00", address, deviceName))
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun subscribeForUpdates(address : String, callback: SocketCallback) {
        interactor.subscribeForConnectionUpdates(address, callback)
    }
}