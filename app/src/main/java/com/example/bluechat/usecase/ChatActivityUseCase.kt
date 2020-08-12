package com.example.bluechat.usecase

import androidx.lifecycle.LiveData
import com.example.bluechat.data.repositories.ChatRepository
import com.example.bluechat.domain.models.Chat
import com.example.bluechat.utils.SocketCallback

class ChatActivityUseCase(var repository: ChatRepository) : UseCase {
    fun sendMessage(message : String, address : String, deviceName : String?) {
        repository.sendMessage(message, address, deviceName)
    }
    fun getChatsFromPartner(partnerAddress : String) : LiveData<ArrayList<Chat>>{
        return repository.getChatsFromPartner(partnerAddress)
    }
    fun subscribeToSocketState(address : String, callback: SocketCallback) {
        repository.subscribeForUpdates(address, callback)
    }
}