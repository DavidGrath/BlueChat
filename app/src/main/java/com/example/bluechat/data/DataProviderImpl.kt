package com.example.bluechat.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.bluechat.domain.models.Chat
import com.example.bluechat.domain.models.Conversation
import com.example.bluechat.domain.models.Device
import kotlin.collections.ArrayList

object DataProviderImpl : DataProvider {
    private var chats = ArrayList<Chat>()
    private val _chatsLiveData = MutableLiveData<ArrayList<Chat>>(chats)
    private val chatsLiveData : LiveData<ArrayList<Chat>> = _chatsLiveData

    private val _chatsWithPartnerLiveData = MutableLiveData<ArrayList<Chat>>(chats.filter { predicate->
        predicate.senderAddress.equals(partner) || predicate.senderAddress.equals("02:00:00:00:00:00")
    }  as ArrayList<Chat>)
    private val chatsWithPartnerLiveData : LiveData<ArrayList<Chat>> =
        _chatsWithPartnerLiveData

    private val conversations = ArrayList<Conversation>()
    private val _conversationsLiveData = MutableLiveData<ArrayList<Conversation>>(conversations)
    val conversationsLiveData : LiveData<ArrayList<Conversation>> =
        _conversationsLiveData

    var partner = ""
    override fun getChats(): LiveData<ArrayList<Chat>> {
        return chatsLiveData
    }
    override fun addChat(chat : Chat) {
        chats.add(chat)
        chats.sortBy { it.timestamp }
        chats.reverse()
        _chatsLiveData.postValue(chats)

        _chatsWithPartnerLiveData.postValue(chats.filter { predicate->
            predicate.senderAddress.equals(partner) || predicate.senderAddress.equals("02:00:00:00:00:00")
        }  as ArrayList<Chat>)
    }

    override fun getChatsFromPartner(partner: String): LiveData<ArrayList<Chat>> {
        DataProviderImpl.partner = partner
        _chatsWithPartnerLiveData.postValue(chats.filter { predicate->
            predicate.senderAddress.equals(partner) || predicate.senderAddress.equals("02:00:00:00:00:00")
        }  as ArrayList<Chat>)
        return chatsWithPartnerLiveData
//        return Transformations.map(chatsLiveData){chats ->
//            chats.filter { it.senderAddress.equals(partner) || it.senderAddress.equals("02:00:00:00:00:00")}  as ArrayList
//        }
    }

    override fun getConversations(): LiveData<ArrayList<Conversation>> {
        return Transformations.map(chatsLiveData) { chats->
            val chatsTemp = ArrayList<Chat>()
            chats.groupBy { chat -> chat.chatPartnerAddress }
                .values
                .forEach { uniqueChat -> chatsTemp.add(uniqueChat.sortedBy { it.timestamp }.last()) }
            val convo = chatsTemp.map { Conversation(Device(it.chatPartnerAddress, it.chatPartnerName), it, 0) }
            convo as ArrayList<Conversation>
        }
    }
}