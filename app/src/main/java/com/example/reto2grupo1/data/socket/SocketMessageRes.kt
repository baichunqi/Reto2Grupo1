package com.example.reto2grupo1.data.socket

import java.util.Date

data class SocketMessageRes (
    val messageType: MessageType,
    val room: String,
    val message: String,
    val authorName: String,
    val authorId: Integer,
    val date : Date,
)
