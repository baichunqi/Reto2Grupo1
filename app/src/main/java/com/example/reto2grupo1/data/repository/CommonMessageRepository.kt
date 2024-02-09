package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.utils.Resource

interface CommonMessageRepository {
    suspend fun getChatMessages(id:Int):Resource<List<Message>>
    suspend fun createMessage(message: Message):Resource<Void>
    suspend fun createOfflineMessage(message: Message):Resource<Void>
    suspend fun changeToSent(message: Message)
    suspend fun clearAllMessages()

}