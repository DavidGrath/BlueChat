package com.example.bluechat.data.repositories

import androidx.lifecycle.LiveData
import com.example.bluechat.data.BluetoothInteractor
import com.example.bluechat.data.DataProvider
import com.example.bluechat.data.PlainMessageHandler
import com.example.bluechat.domain.models.Chat
import java.util.*
import kotlin.collections.ArrayList

class ChatRepository(private var provider : DataProvider,private var interactor: BluetoothInteractor) {

    init {
        interactor.subscribeForMessages(object : PlainMessageHandler{
            override fun onPlainMessageSent(address: String, content: String) {
                provider.addChat(Chat(content, Date().time, address))
            }
        })
    }

    fun getChatsFromPartner(partnerAddress : String) : LiveData<ArrayList<Chat>>{
        return provider.getChatsFromPartner(partnerAddress)
    }


    fun sendMessage(message : String) {
        try{
            interactor.write(message.toByteArray())
            provider.addChat(Chat(message, Date().time, "02:00:00:00:00:00"))
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}