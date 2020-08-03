package com.example.bluechat.domain.models

data class Conversation(val user : Device, val lastChat : Chat, val unreadCount : Int) {
}