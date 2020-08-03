package com.example.bluechat.domain.models

data class Chat(val content : String, var timestamp : Long, var senderAddress : String, var senderName : String? = null) {

}