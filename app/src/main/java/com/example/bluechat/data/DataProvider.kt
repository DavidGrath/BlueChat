package com.example.bluechat.data

import androidx.lifecycle.LiveData
import com.example.bluechat.domain.models.Chat
import com.example.bluechat.domain.models.Conversation

interface DataProvider {
    fun getChats() : LiveData<ArrayList<Chat>>
    fun addChat(chat: Chat)
    fun getChatsFromPartner(partner : String) : LiveData<ArrayList<Chat>>
    fun getConversations() : LiveData<ArrayList<Conversation>>
}