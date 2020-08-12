package com.example.bluechat.utils

import java.util.*

class Constants {
    companion object {
        val RFCOMM_UUID : UUID = UUID.fromString("e2028cf8-7513-4a63-8835-961503bcea82")
        val INTENT_CHATPARTNER_ADDRESS = "chatpartner_address"
        val INTENT_CHATPARTNER_NAME = "chatpartner_name"
        val OWN_ADDRESS = "02:00:00:00:00:00"
    }
}