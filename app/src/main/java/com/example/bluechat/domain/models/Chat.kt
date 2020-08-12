package com.example.bluechat.domain.models

import com.example.bluechat.utils.Constants.Companion.OWN_ADDRESS

data class Chat(val content : String, var timestamp : Long, var senderAddress : String,
                var recipientAddress : String,
                var chatPartnerName : String? = null,
                var chatPartnerAddress : String = if(senderAddress == OWN_ADDRESS) recipientAddress else senderAddress) {

}