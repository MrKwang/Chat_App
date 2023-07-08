package com.example.myapplication.model

import java.util.*

data class RecentlyChatUser(
    var username: String,
    var image: String,
    var id: String,
    var lastMess: String,
    var time: Date,
    var sendTime: String
)
