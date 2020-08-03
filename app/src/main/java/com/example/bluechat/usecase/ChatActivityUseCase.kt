package com.example.bluechat.usecase

import androidx.lifecycle.LiveData
import com.example.bluechat.data.repositories.ChatRepository
import com.example.bluechat.domain.models.Chat

class ChatActivityUseCase(var repository: ChatRepository) : UseCase {
    fun sendMessage(message : String) {
        repository.sendMessage(message)
    }
    fun getChatsFromPartner(partnerAddress : String) : LiveData<ArrayList<Chat>>{
        return repository.getChatsFromPartner(partnerAddress)
    }
}