package com.example.myapplication.model

import java.util.*

data class ChatMessage (
    val id: String,
    var sendId: String,
    var receiveId: String,
    var message: String,
    var timeStamp: Date,
    var day: String,
    var time: String,
    var showAva : Boolean = false
   )


