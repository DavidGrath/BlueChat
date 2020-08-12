package com.example.bluechat.data

interface PlainMessageHandler {
    fun onPlainMessageSent(address : String, content : String, senderName : String?)
}