package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.utils.Resource

interface ChatRepository {
    suspend fun getChat(chat : Chat):Resource<List<Message>>
     suspend fun getChatMessages(id : Int) : Resource<List<Message>>
     suspend fun joinChat( chatId :Int): Resource<Boolean>
     suspend fun assignUser(chatId : Int, userId: Int): Resource<Void>

}